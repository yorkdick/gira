from datetime import datetime, UTC
from app import db


class Project(db.Model):
    __tablename__ = "projects"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    key = db.Column(db.String(10), unique=True, nullable=False)
    description = db.Column(db.Text)
    status = db.Column(db.String(20), default='active')
    owner_id = db.Column(db.Integer, db.ForeignKey('users.id'), nullable=False)
    created_at = db.Column(db.DateTime, default=lambda: datetime.now(UTC))
    updated_at = db.Column(
        db.DateTime, 
        default=lambda: datetime.now(UTC),
        onupdate=lambda: datetime.now(UTC)
    )

    # 关系
    owner = db.relationship('User', backref=db.backref('owned_projects', lazy=True))

    def __repr__(self):
        return f"<Project {self.key}>"

    def to_dict(self):
        """将项目对象序列化为字典"""
        return {
            'id': self.id,
            'name': self.name,
            'key': self.key,
            'description': self.description,
            'status': self.status,
            'owner_id': self.owner_id,
            'created_at': self.created_at.isoformat() if self.created_at else None,
            'updated_at': self.updated_at.isoformat() if self.updated_at else None
        }
