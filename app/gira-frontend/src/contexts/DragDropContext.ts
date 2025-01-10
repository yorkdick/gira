import { createContext } from 'react';
import { useDrag } from '@/hooks/useDrag';

interface DragDropContextValue {
  onDragStart: ReturnType<typeof useDrag>['onDragStart'];
  onDragOver: ReturnType<typeof useDrag>['onDragOver'];
  onDrop: ReturnType<typeof useDrag>['onDrop'];
}

export const DragDropContext = createContext<DragDropContextValue | null>(null); 