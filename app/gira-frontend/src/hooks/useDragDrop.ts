import { useContext } from 'react';
import { DragDropContext } from '../contexts/DragDropContext';

export const useDragDrop = () => {
  const context = useContext(DragDropContext);
  if (!context) {
    throw new Error('useDragDrop must be used within a DragDropProvider');
  }
  return context;
}; 