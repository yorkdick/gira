// ... existing code ...

// 创建新的 Sprint
function createSprint() {
    const name = prompt('新しいスプリント名を入力してください：');
    if (!name) return;

    const goal = prompt('スプリントの目標を入力してください（任意）：');

    fetch('/api/sprints/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({ name, goal })
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            showError(data.error);
        } else {
            location.reload();
        }
    })
    .catch(error => {
        showError('スプリントの作成に失敗しました');
        console.error('Error:', error);
    });
}

// Sprint を開始
function startSprint(sprintId) {
    if (!confirm('このスプリントを開始しますか？')) return;

    fetch(`/api/sprints/${sprintId}/start`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            showError(data.error);
        } else {
            location.reload();
        }
    })
    .catch(error => {
        showError('スプリントの開始に失敗しました');
        console.error('Error:', error);
    });
}

// Sprint を完了
function completeSprint(sprintId) {
    if (!confirm('このスプリントを完了としてマークしますか？')) return;

    fetch(`/api/sprints/${sprintId}/complete`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.error) {
            showError(data.error);
        } else {
            location.reload();
        }
    })
    .catch(error => {
        showError('スプリントの完了に失敗しました');
        console.error('Error:', error);
    });
}

// エラーメッセージを表示
function showError(message) {
    const errorDiv = document.createElement('div');
    errorDiv.className = 'alert alert-danger alert-dismissible fade show position-fixed top-0 start-50 translate-middle-x mt-3';
    errorDiv.setAttribute('role', 'alert');
    errorDiv.style.zIndex = '1050';
    errorDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `;
    document.body.appendChild(errorDiv);
    
    // 3秒後に自動的に消える
    setTimeout(() => {
        errorDiv.remove();
    }, 3000);
}

// ... existing code ... 