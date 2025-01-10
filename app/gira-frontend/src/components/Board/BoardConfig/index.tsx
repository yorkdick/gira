import React, { useState } from 'react';
import { Button, Form, Input, Modal, Space, Table, message } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { usePermission } from '@/hooks/usePermission';
import { BoardColumn } from '@/types/board';

interface BoardConfigProps {
  visible: boolean;
  onClose: () => void;
  boardColumns: BoardColumn[];
  onSave: (columns: BoardColumn[]) => void;
}

export const BoardConfig: React.FC<BoardConfigProps> = ({
  visible,
  onClose,
  boardColumns,
  onSave,
}) => {
  const { isAdmin } = usePermission();
  const [editingColumns, setEditingColumns] = useState<BoardColumn[]>(boardColumns);
  const [editingColumnId, setEditingColumnId] = useState<number | null>(null);
  const [form] = Form.useForm();

  const handleAdd = () => {
    form.validateFields().then((values) => {
      const newColumn: BoardColumn = {
        id: Date.now(),
        name: values.name,
        wipLimit: Number(values.wipLimit),
        order: editingColumns.length,
      };
      setEditingColumns([...editingColumns, newColumn]);
      form.resetFields();
    });
  };

  const handleDelete = (id: number) => {
    setEditingColumns(editingColumns.filter((col) => col.id !== id));
  };

  const handleEdit = (record: BoardColumn) => {
    setEditingColumnId(record.id);
    form.setFieldsValue({
      name: record.name,
      wipLimit: record.wipLimit,
    });
  };

  const handleUpdate = () => {
    if (editingColumnId === null) return;
    
    form.validateFields().then((values) => {
      setEditingColumns(
        editingColumns.map((col) =>
          col.id === editingColumnId
            ? {
                ...col,
                name: values.name,
                wipLimit: Number(values.wipLimit),
              }
            : col
        )
      );
      setEditingColumnId(null);
      form.resetFields();
    });
  };

  const handleSave = () => {
    onSave(editingColumns);
    message.success('看板配置已保存');
    onClose();
  };

  const tableColumns: ColumnsType<BoardColumn> = [
    {
      title: '列名',
      dataIndex: 'name',
      key: 'name',
      render: (text: string, record: BoardColumn) =>
        editingColumnId === record.id ? (
          <Form.Item
            name="name"
            rules={[{ required: true, message: '请输入列名' }]}
            style={{ margin: 0 }}
          >
            <Input />
          </Form.Item>
        ) : (
          text
        ),
    },
    {
      title: 'WIP限制',
      dataIndex: 'wipLimit',
      key: 'wipLimit',
      render: (text: number, record: BoardColumn) =>
        editingColumnId === record.id ? (
          <Form.Item
            name="wipLimit"
            rules={[{ required: true, message: '请输入WIP限制' }]}
            style={{ margin: 0 }}
          >
            <Input type="number" />
          </Form.Item>
        ) : (
          text
        ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record: BoardColumn) => (
        <Space>
          {editingColumnId === record.id ? (
            <>
              <Button type="link" onClick={handleUpdate}>
                保存
              </Button>
              <Button type="link" onClick={() => setEditingColumnId(null)}>
                取消
              </Button>
            </>
          ) : (
            <>
              <Button
                icon={<EditOutlined />}
                onClick={() => handleEdit(record)}
              />
              <Button
                danger
                icon={<DeleteOutlined />}
                onClick={() => handleDelete(record.id)}
              />
            </>
          )}
        </Space>
      ),
    },
  ];

  return (
    <Modal
      title="看板配置"
      open={visible}
      onCancel={onClose}
      width={800}
      footer={[
        <Button key="cancel" onClick={onClose}>
          取消
        </Button>,
        <Button key="save" type="primary" onClick={handleSave}>
          保存
        </Button>,
      ]}
    >
      {isAdmin ? (
        <>
          <Form form={form} layout="inline" style={{ marginBottom: 16 }}>
            <Form.Item
              name="name"
              rules={[{ required: true, message: '请输入列名' }]}
            >
              <Input placeholder="列名" />
            </Form.Item>
            <Form.Item
              name="wipLimit"
              rules={[{ required: true, message: '请输入WIP限制' }]}
            >
              <Input type="number" placeholder="WIP限制" min={0} />
            </Form.Item>
            <Form.Item>
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleAdd}
                disabled={editingColumnId !== null}
              >
                添加列
              </Button>
            </Form.Item>
          </Form>
          <Table
            columns={tableColumns}
            dataSource={editingColumns}
            rowKey="id"
            pagination={false}
          />
        </>
      ) : (
        <div>您没有权限配置看板</div>
      )}
    </Modal>
  );
}; 