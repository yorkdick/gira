import unittest
from app import create_app, db
from app.models.backlog import Backlog
from flask import url_for

class TestBacklogViews(unittest.TestCase):
    def setUp(self):
        self.app = create_app('testing')
        self.client = self.app.test_client()
        with self.app.app_context():
            db.create_all()
            backlog = Backlog(title='Test Story')
            db.session.add(backlog)
            db.session.commit()

    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()

    def test_add_story(self):
        response = self.client.post(url_for('backlog.add'), data={
            'title': 'New Story',
            'description': 'Story Description'
        }, follow_redirects=True)
        self.assertIn('ストーリーが追加されました', response.data)

    def test_edit_story(self):
        response = self.client.post(url_for('backlog.edit', id=1), data={
            'title': 'Updated Story',
            'description': 'Updated Description'
        }, follow_redirects=True)
        self.assertIn('ストーリーが更新されました', response.data)

if __name__ == '__main__':
    unittest.main() 