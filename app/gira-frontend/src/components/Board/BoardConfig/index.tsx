import React, { useState } from 'react';
import {
  Modal,
  Form,
  Input,
  Button,
  Space,
  List,
  InputNumber,
  Popconfirm,
} from 'antd';
import {
  PlusOutlined,
  DeleteOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '@/store';
import { Board, BoardColumn, UpdateBoardParams } from '@/types/board';
import { updateBoard } from '@/store/slices/boardSlice';
import styles from './style.module.less';

interface ExtendedBoardColumn extends BoardColumn {
  wipLimit?: number;
}

interface ExtendedBoard extends Board {
  description?: string;
}

interface BoardConfigProps {
  board: ExtendedBoard;
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const BoardConfig: React.FC<BoardConfigProps> = ({
  board,
  visible,
  onClose,
  onSuccess,
}) => {
  const [form] = Form.useForm();
  const dispatch = useDispatch<AppDispatch>();
  const [columns, setColumns] = useState<ExtendedBoardColumn[]>(
    board.columns.map((col) => ({
      ...col,
      wipLimit: 0,
    }))
  );

  const handleSubmit = async () => {
    const values = await form.validateFields();
    try {
      const params: UpdateBoardParams = {
        name: values.name,
        columns: columns.map((col) => ({
          id: col.id,
          name: col.name,
          order: col.order,
        })),
      };
      await dispatch(updateBoard({ id: board.id, params }));
      onSuccess();
      onClose();
    } catch {
      // 错误已在slice中处理
    }
  };

  const handleAddColumn = () => {
    const newColumn: ExtendedBoardColumn = {
      id: Date.now(), // 临时ID，保存时后端会分配真实ID
      name: '',
      order: columns.length,
      taskIds: [],
      wipLimit: 0,
    };
    setColumns([...columns, newColumn]);
  };

  const handleDeleteColumn = (index: number) => {
    const newColumns = [...columns];
    newColumns.splice(index, 1);
    // 更新order
    newColumns.forEach((col, idx) => {
      col.order = idx;
    });
    setColumns(newColumns);
  };

  const handleMoveColumn = (index: number, direction: 'up' | 'down') => {
    if (
      (direction === 'up' && index === 0) ||
      (direction === 'down' && index === columns.length - 1)
    ) {
      return;
    }

    const newColumns = [...columns];
    const targetIndex = direction === 'up' ? index - 1 : index + 1;
    [newColumns[index], newColumns[targetIndex]] = [
      newColumns[targetIndex],
      newColumns[index],
    ];
    // 更新order
    newColumns.forEach((col, idx) => {
      col.order = idx;
    });
    setColumns(newColumns);
  };

  const handleColumnChange = (
    index: number,
    field: keyof ExtendedBoardColumn,
    value: string | number
  ) => {
    const newColumns = [...columns];
    newColumns[index] = {
      ...newColumns[index],
      [field]: value,
    };
    setColumns(newColumns);
  };

  return (
    <Modal
      title="看板配置"
      open={visible}
      onCancel={onClose}
      width={600}
      footer={[
        <Button key="cancel" onClick={onClose}>
          取消
        </Button>,
        <Button key="submit" type="primary" onClick={handleSubmit}>
          保存
        </Button>,
      ]}
    >
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          name: board.name,
          description: board.description || '',
        }}
      >
        <Form.Item
          name="name"
          label="看板名称"
          rules={[{ required: true, message: '请输入看板名称' }]}
        >
          <Input placeholder="请输入看板名称" />
        </Form.Item>

        <Form.Item name="description" label="看板描述">
          <Input.TextArea
            placeholder="请输入看板描述"
            autoSize={{ minRows: 2, maxRows: 6 }}
          />
        </Form.Item>

        <div className={styles.columnsSection}>
          <div className={styles.columnsSectionHeader}>
            <h4>看板列配置</h4>
            <Button
              type="primary"
              icon={<PlusOutlined />}
              onClick={handleAddColumn}
            >
              添加列
            </Button>
          </div>

          <List
            className={styles.columnsList}
            dataSource={columns}
            renderItem={(column, index) => (
              <List.Item className={styles.columnItem}>
                <div className={styles.columnContent}>
                  <Input
                    value={column.name}
                    onChange={(e) =>
                      handleColumnChange(index, 'name', e.target.value)
                    }
                    placeholder="列名称"
                    className={styles.columnName}
                  />
                  <InputNumber
                    value={column.wipLimit}
                    onChange={(value) =>
                      handleColumnChange(index, 'wipLimit', value || 0)
                    }
                    min={0}
                    placeholder="WIP限制"
                    className={styles.wipLimit}
                  />
                  <Space>
                    <Button
                      type="text"
                      icon={<ArrowUpOutlined />}
                      onClick={() => handleMoveColumn(index, 'up')}
                      disabled={index === 0}
                    />
                    <Button
                      type="text"
                      icon={<ArrowDownOutlined />}
                      onClick={() => handleMoveColumn(index, 'down')}
                      disabled={index === columns.length - 1}
                    />
                    <Popconfirm
                      title="确定要删除这一列吗？"
                      onConfirm={() => handleDeleteColumn(index)}
                      okText="确定"
                      cancelText="取消"
                    >
                      <Button
                        type="text"
                        danger
                        icon={<DeleteOutlined />}
                        disabled={columns.length <= 1}
                      />
                    </Popconfirm>
                  </Space>
                </div>
              </List.Item>
            )}
          />
        </div>
      </Form>
    </Modal>
  );
};

export default BoardConfig; 