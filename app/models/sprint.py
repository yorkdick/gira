from datetime import datetime
from app import db

class Sprint(db.Model):
    __tablename__ = 'sprints'

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    start_date = db.Column(db.DateTime)
    end_date = db.Column(db.DateTime)
    status = db.Column(db.String(20), default='planning')  # planning, active, completed
    project_id = db.Column(db.Integer, db.ForeignKey('projects.id'), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    goal = db.Column(db.String(255))

    # 关联关系
    project = db.relationship('Project', backref=db.backref('sprints', lazy=True))

    def __repr__(self):
        return f'<Sprint {self.name}>' 