"""add initial projects

Revision ID: add_initial_projects
Revises: add_initial_users
Create Date: 2024-12-05 14:45:00.000000

"""
from alembic import op
import sqlalchemy as sa
from datetime import datetime

# revision identifiers, used by Alembic.
revision = 'add_initial_projects'
down_revision = 'add_initial_users'
branch_labels = None
depends_on = None

def upgrade():
    # プロジェクトテーブルの作成
    op.create_table(
        'projects',
        sa.Column('id', sa.Integer(), nullable=False),
        sa.Column('name', sa.String(100), nullable=False),
        sa.Column('key', sa.String(10), nullable=False),
        sa.Column('description', sa.Text(), nullable=True),
        sa.Column('created_at', sa.DateTime(), nullable=False),
        sa.Column('updated_at', sa.DateTime(), nullable=False),
        sa.PrimaryKeyConstraint('id'),
        sa.UniqueConstraint('key')
    )

    # サンプルプロジェクトの追加
    projects_table = sa.table('projects',
        sa.column('name', sa.String),
        sa.column('key', sa.String),
        sa.column('description', sa.Text),
        sa.column('created_at', sa.DateTime),
        sa.column('updated_at', sa.DateTime)
    )

    op.bulk_insert(projects_table,
        [
            {
                'name': 'サンプルプロジェクト1',
                'key': 'SAMPLE1',
                'description': 'これはサンプルプロジェクト1です',
                'created_at': datetime.utcnow(),
                'updated_at': datetime.utcnow()
            },
            {
                'name': 'サンプルプロジェクト2',
                'key': 'SAMPLE2',
                'description': 'これはサンプルプロジェクト2です',
                'created_at': datetime.utcnow(),
                'updated_at': datetime.utcnow()
            }
        ]
    )

def downgrade():
    op.drop_table('projects') 