import React, { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { Spin, Row, Col } from 'antd';
import { RootState, AppDispatch } from '@/store';
import { fetchBoard } from '@/store/slices/boardSlice';
import { fetchTasks } from '@/store/slices/taskSlice';
import { BoardColumn as BoardColumnType } from '@/types/board';
import BoardColumn from '@/components/Board/BoardColumn';
import BoardFilter from '@/components/Board/BoardFilter';
import styles from './style.module.less';

const Board: React.FC = () => {
  const { boardId } = useParams<{ boardId: string }>();
  const dispatch = useDispatch<AppDispatch>();
  const { entities, currentBoardId, loading: boardLoading } = useSelector((state: RootState) => state.board);
  const currentBoard = currentBoardId ? entities.byId[currentBoardId] : null;
  const { loading: taskLoading } = useSelector((state: RootState) => state.task);

  useEffect(() => {
    if (boardId) {
      dispatch(fetchBoard(Number(boardId)));
      dispatch(fetchTasks({ boardId: Number(boardId) }));
    }
  }, [boardId, dispatch]);

  const loading = boardLoading || taskLoading;

  if (loading) {
    return (
      <div className={styles.loading}>
        <Spin size="large" />
      </div>
    );
  }

  if (!currentBoard) {
    return <div>看板不存在</div>;
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h2 className={styles.title}>{currentBoard.name}</h2>
        <BoardFilter />
      </div>
      <div className={styles.content}>
        <Row gutter={16} wrap={false} className={styles.columns}>
          {currentBoard.columns.map((column: BoardColumnType) => (
            <Col key={column.id} className={styles.column}>
              <BoardColumn column={column} />
            </Col>
          ))}
        </Row>
      </div>
    </div>
  );
};

export default Board; 