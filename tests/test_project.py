import unittest
from app import create_app, db
from app.models.project import Project
from flask import url_for

class TestProjectViews(unittest.TestCase):
    def setUp(self):
        self.app = create_app('testing')
        self.client = self.app.test_client()
        with self.app.app_context():
            db.create_all()
            project = Project(name='Test Project')
            db.session.add(project)
            db.session.commit()

    def tearDown(self):
        with self.app.app_context():
            db.session.remove()
            db.drop_all()

    def test_create_project(self):
        response = self.client.post(url_for('project.create'), data={
            'name': 'New Project',
            'description': 'Project Description'
        }, follow_redirects=True)
        self.assertIn('プロジェクトが作成されました', response.data)

    def test_edit_project(self):
        response = self.client.post(url_for('project.edit', id=1), data={
            'name': 'Updated Project',
            'description': 'Updated Description'
        }, follow_redirects=True)
        self.assertIn('プロジェクトが更新されました', response.data)

if __name__ == '__main__':
    unittest.main() 