from flask import Blueprint, render_template, jsonify, request, session
from sqlalchemy import or_
from app.models.project import Project
from app.extensions import db

bp = Blueprint('project', __name__)

@bp.route('/projects')
@bp.route('/projects/<int:project_id>')
def index(project_id=None):
    """项目一览页面"""
    # 获取当前项目（如果有）
    current_project = None
    if project_id:
        current_project = Project.query.get_or_404(project_id)
        session['project_id'] = project_id
    elif 'project_id' in session:
        current_project = Project.query.get(session['project_id'])
    
    # 获取所有项目用于下拉菜单
    projects = Project.query.filter(Project.status != 'deleted').all()
    
    return render_template('project/index.html',
                         current_project=current_project,
                         projects=projects)

@bp.route('/api/project/list')
def list_projects():
    """获取项目列表API"""
    # 获取搜索参数
    name = request.args.get('name', '')
    status = request.args.get('status', '')
    
    # 构建查询
    query = Project.query.filter(Project.status != 'deleted')
    
    # 添加搜索条件
    if name:
        query = query.filter(Project.name.like(f'%{name}%'))
    if status:
        query = query.filter(Project.status == status)
        
    # 按创建时间倒序排序
    query = query.order_by(Project.created_at.desc())
    
    # 执行查询
    projects = query.all()
    
    return jsonify([{
        'id': p.id,
        'name': p.name,
        'description': p.description,
        'status': p.status,
        'created_at': p.created_at.strftime('%Y/%m/%d'),
        'key': p.key
    } for p in projects])

@bp.route('/api/project/create', methods=['POST'])
def create_project():
    """创建项目API"""
    data = request.get_json()
    
    # 生成项目key
    last_project = Project.query.order_by(Project.id.desc()).first()
    next_id = (last_project.id + 1) if last_project else 1
    key = f'PRJ-{next_id:04d}'
    
    project = Project(
        name=data['name'],
        key=key,  # 添加key
        description=data.get('description', ''),
        status=data.get('status', 'active')
    )
    db.session.add(project)
    db.session.commit()
    return jsonify({'status': 'success'})

@bp.route('/api/project/update', methods=['POST'])
def update_project():
    """更新项目API"""
    data = request.get_json()
    project = Project.query.get_or_404(data['id'])
    project.name = data['name']
    project.description = data.get('description', '')
    project.status = data.get('status', 'active')
    db.session.commit()
    return jsonify({'status': 'success'})

@bp.route('/api/project/delete', methods=['POST'])
def delete_project():
    """删除项目API"""
    data = request.get_json()
    project = Project.query.get_or_404(data['id'])
    project.status = 'deleted'
    db.session.commit()
    return jsonify({'status': 'success'})

@bp.route('/projects/<int:project_id>')
def detail(project_id):
    """项目详情页面"""
    project = Project.query.get_or_404(project_id)
    return render_template('project/detail.html', project=project) 