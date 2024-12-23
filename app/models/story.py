from datetime import datetime
from app import db

class Story(db.Model):
    __tablename__ = 'stories'

    PRIORITY_NONE = 0
    PRIORITY_LOW = 1
    PRIORITY_MEDIUM = 2
    PRIORITY_HIGH = 3
    PRIORITY_HIGHEST = 4

    PRIORITY_CHOICES = {
        PRIORITY_NONE: {'name': 'None', 'color': '#6B778C', 'icon': ''},
        PRIORITY_LOW: {'name': 'Low', 'color': '#2D8738', 'icon': '↓'},
        PRIORITY_MEDIUM: {'name': 'Medium', 'color': '#0052CC', 'icon': '='},
        PRIORITY_HIGH: {'name': 'High', 'color': '#CD5A19', 'icon': '↑'},
        PRIORITY_HIGHEST: {'name': 'Highest', 'color': '#CD1F1F', 'icon': '↑↑'}
    }

    # 看板状态
    KANBAN_TODO = 'todo'
    KANBAN_DOING = 'doing'
    KANBAN_DONE = 'done'

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.String(200), nullable=False)
    description = db.Column(db.Text)
    status = db.Column(db.String(20), default='todo')  # 看板状态：todo, doing, done
    story_points = db.Column(db.Integer)
    priority = db.Column(db.Integer, default=PRIORITY_NONE)
    project_id = db.Column(db.Integer, db.ForeignKey('projects.id'), nullable=False)
    sprint_id = db.Column(db.Integer, db.ForeignKey('sprints.id'))
    assignee_id = db.Column(db.Integer, db.ForeignKey('users.id'))
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    updated_at = db.Column(db.DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    # 关联关系
    project = db.relationship('Project', backref=db.backref('stories', lazy=True))
    sprint = db.relationship('Sprint', backref=db.backref('stories', lazy=True))
    assignee = db.relationship('User', backref=db.backref('assigned_stories', lazy=True))

    @property
    def priority_info(self):
        """获取优先级信息"""
        return self.PRIORITY_CHOICES.get(self.priority, self.PRIORITY_CHOICES[self.PRIORITY_NONE])

    def __repr__(self):
        return f'<Story {self.title}>' 