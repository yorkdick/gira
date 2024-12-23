import unittest
from app import create_app, db
from app.models.kanban import Kanban
from flask import url_for

class TestKanbanViews(unittest.TestCase):
    def setUp(self):
        self.app = create_app('testing')
        self.client = self.app.test_client()
        with self.app.app_context():
            db.create_all()
            kanban = Kanban(title='Test Task')
            db.session.add(kanban)
            db.session.commit()

    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()

    def test_move_task(self):
        response = self.client.post(url_for('kanban.move', id=1), data={
            'status': 'In Progress'
        }, follow_redirects=True)
        self.assertIn('タスクが移動されました', response.data)

    def test_delete_task(self):
        response = self.client.post(url_for('kanban.delete', id=1), follow_redirects=True)
        self.assertIn('タスクが削除されました', response.data)

if __name__ == '__main__':
    unittest.main() 