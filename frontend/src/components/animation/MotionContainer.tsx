import React from 'react';
import { motion, HTMLMotionProps } from 'framer-motion';

export interface MotionContainerProps extends HTMLMotionProps<'div'> {
  delay?: number;
  duration?: number;
}

export const MotionContainer: React.FC<MotionContainerProps> = ({
  children,
  delay = 0,
  duration = 0.4,
  className,
  ...props
}) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 12 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -12 }}
      transition={{ duration, delay, ease: 'easeOut' }}
      className={className}
      {...props}
    >
      {children}
    </motion.div>
  );
};
