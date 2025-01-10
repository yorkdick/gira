import React from 'react';
import { useDrag } from '@/hooks/useDrag';
import { DragDropContext } from '../contexts/DragDropContext';

interface DragDropProviderProps {
  children: React.ReactNode;
}

const DragDropProvider: React.FC<DragDropProviderProps> = ({ children }) => {
  const { onDragStart, onDragOver, onDrop } = useDrag();

  return (
    <DragDropContext.Provider value={{ onDragStart, onDragOver, onDrop }}>
      {children}
    </DragDropContext.Provider>
  );
};

export default DragDropProvider; 