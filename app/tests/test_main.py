import unittest
from app import create_app
from flask import url_for

class TestMainViews(unittest.TestCase):
    def setUp(self):
        self.app = create_app('testing')
        self.client = self.app.test_client()

    def test_home_page(self):
        response = self.client.get(url_for('main.index'))
        self.assertEqual(response.status_code, 200)
        self.assertIn(b'Welcome to the Home Page', response.data)

if __name__ == '__main__':
    unittest.main() 