import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  private client: Client | null = null;
  private isConnected = false;
  private connectionPromise: Promise<void> | null = null;

  public connect(): Promise<void> {
    if (this.isConnected && this.client) {
      return Promise.resolve();
    }

    if (this.connectionPromise) {
      return this.connectionPromise;
    }

    this.connectionPromise = new Promise((resolve, reject) => {
      const token = localStorage.getItem('devarena_token') || '';

      this.client = new Client({
        webSocketFactory: () => {
          try {
            return new SockJS('/ws');
          } catch (e) {
            console.warn('SockJS fallback, using native WebSocket:', e);
            const proto = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            return new WebSocket(`${proto}//${window.location.host}/ws-direct`);
          }
        },
        connectHeaders: {
          Authorization: `Bearer ${token}`,
          token,
        },
        debug: (str) => {
          if (import.meta.env.DEV && false) {
            console.log('[STOMP]', str);
          }
        },
        reconnectDelay: 3000,
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000,
        onConnect: () => {
          this.isConnected = true;
          this.connectionPromise = null;
          resolve();
        },
        onStompError: (frame) => {
          console.error('Broker reported error: ' + frame.headers['message']);
          console.error('Additional details: ' + frame.body);
          this.isConnected = false;
          this.connectionPromise = null;
          reject(new Error(frame.headers['message'] || 'STOMP error'));
        },
        onWebSocketClose: () => {
          this.isConnected = false;
          this.connectionPromise = null;
        },
      });

      this.client.activate();
    });

    return this.connectionPromise;
  }

  public disconnect(): void {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.isConnected = false;
      this.connectionPromise = null;
    }
  }

  public subscribe(destination: string, callback: (message: any) => void): () => void {
    if (!this.client || !this.isConnected) {
      // If not connected yet, connect first then subscribe
      this.connect().then(() => {
        if (this.client) {
          const sub = this.client.subscribe(destination, (msg: IMessage) => {
            try {
              callback(JSON.parse(msg.body));
            } catch {
              callback(msg.body);
            }
          });
          return () => sub.unsubscribe();
        }
      });
      return () => {};
    }

    const subscription = this.client.subscribe(destination, (msg: IMessage) => {
      try {
        callback(JSON.parse(msg.body));
      } catch {
        callback(msg.body);
      }
    });

    return () => {
      try {
        subscription.unsubscribe();
      } catch {
        // ignore
      }
    };
  }

  public send(destination: string, body: any = {}): void {
    if (this.client && this.isConnected) {
      this.client.publish({
        destination,
        body: JSON.stringify(body),
      });
    }
  }
}

export const webSocketService = new WebSocketService();
