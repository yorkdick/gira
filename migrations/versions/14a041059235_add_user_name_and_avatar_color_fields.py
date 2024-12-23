"""Add user name and avatar color fields

Revision ID: 14a041059235
Revises: ee92b7660010
Create Date: 2024-12-22 14:16:16.908016

"""
from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision = '14a041059235'
down_revision = 'ee92b7660010'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('users', schema=None) as batch_op:
        batch_op.add_column(sa.Column('first_name', sa.String(length=64), nullable=True))
        batch_op.add_column(sa.Column('last_name', sa.String(length=64), nullable=True))
        batch_op.add_column(sa.Column('avatar_color', sa.String(length=7), nullable=True))

    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    with op.batch_alter_table('users', schema=None) as batch_op:
        batch_op.drop_column('avatar_color')
        batch_op.drop_column('last_name')
        batch_op.drop_column('first_name')

    # ### end Alembic commands ###
