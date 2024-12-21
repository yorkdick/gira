"""add initial users

Revision ID: add_initial_users
Revises: 849235d97a38
Create Date: 2024-12-05 14:30:00.000000

"""
from alembic import op
import sqlalchemy as sa
from datetime import datetime
from werkzeug.security import generate_password_hash

# revision identifiers, used by Alembic.
revision = 'add_initial_users'
down_revision = '849235d97a38'
branch_labels = None
depends_on = None

def upgrade():
    # テストユーザーデータの作成
    users_table = sa.table('users',
        sa.column('id', sa.Integer),
        sa.column('username', sa.String),
        sa.column('email', sa.String),
        sa.column('password_hash', sa.String),
        sa.column('is_active', sa.Boolean),
        sa.column('created_at', sa.DateTime),
        sa.column('last_login', sa.DateTime)
    )

    # 初期ユーザーデータ
    op.bulk_insert(users_table,
        [
            {
                'username': 'admin',
                'email': 'admin@example.com',
                'password_hash': generate_password_hash('admin123'),
                'is_active': True,
                'created_at': datetime.utcnow(),
                'last_login': None
            },
            {
                'username': 'test_user',
                'email': 'test@example.com',
                'password_hash': generate_password_hash('test123'),
                'is_active': True,
                'created_at': datetime.utcnow(),
                'last_login': None
            }
        ]
    )

def downgrade():
    # 追加したユーザーデータを削除
    op.execute('DELETE FROM users WHERE username IN ("admin", "test_user")') 