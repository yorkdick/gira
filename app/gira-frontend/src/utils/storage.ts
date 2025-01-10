// 封装localStorage操作
const storage = {
  // 设置
  set<T>(key: string, value: T): void {
    if (typeof value === 'object') {
      localStorage.setItem(key, JSON.stringify(value));
    } else {
      localStorage.setItem(key, String(value));
    }
  },

  // 获取
  get<T>(key: string): T | null {
    const value = localStorage.getItem(key);
    if (value) {
      try {
        return JSON.parse(value);
      } catch {
        return value as unknown as T;
      }
    }
    return null;
  },

  // 删除
  remove(key: string): void {
    localStorage.removeItem(key);
  },

  // 清空
  clear(): void {
    localStorage.clear();
  },

  // 获取所有key
  keys(): string[] {
    const keys: string[] = [];
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i);
      if (key) {
        keys.push(key);
      }
    }
    return keys;
  }
};

export default storage; 