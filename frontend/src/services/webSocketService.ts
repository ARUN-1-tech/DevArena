import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

/**
 * Resolves WebSocket and SockJS connection URLs based on VITE_API_BASE_URL or browser origin.
 */
export const getWebSocketEndpoints = (): { sockJsUrl: string; nativeWsUrl: string } => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;

  if (envUrl && (envUrl.startsWith('http://') || envUrl.startsWith('https://'))) {
    try {
      const url = new URL(envUrl);
      const isHttps = url.protocol === 'https:';
      const wsProto = isHttps ? 'wss:' : 'ws:';
      const host = url.host;

      return {
        sockJsUrl: `${url.protocol}//${host}/ws`,
        nativeWsUrl: `${wsProto}//${host}/ws-direct`,
      };
    } catch (e) {
      console.warn('Failed to parse VITE_API_BASE_URL for WebSocket:', e);
    }
  }

  // Local development / same-origin fallback
  const isHttps = typeof window !== 'undefined' && window.location.protocol === 'https:';
  const proto = isHttps ? 'wss:' : 'ws:';
  const host = typeof window !== 'undefined' ? window.location.host : 'localhost:5173';

  return {
    sockJsUrl: '/ws',
    nativeWsUrl: `${proto}//${host}/ws-direct`,
  };
};

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
      const { sockJsUrl, nativeWsUrl } = getWebSocketEndpoints();

      this.client = new Client({
        webSocketFactory: () => {
          try {
            return new SockJS(sockJsUrl);
          } catch (e) {
            console.warn('SockJS fallback, using native WebSocket:', e);
            return new WebSocket(nativeWsUrl);
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
