from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from webdriver_manager.chrome import ChromeDriverManager
import os
import time

def setup_chrome_driver():
    chrome_options = Options()
    chrome_options.add_argument('--headless')  # ヘッドレスモードで実行
    chrome_options.add_argument('--window-size=1920,1080')  # 画面サイズを設定
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')
    
    service = Service(ChromeDriverManager().install())
    driver = webdriver.Chrome(service=service, options=chrome_options)
    return driver

def capture_login_page():
    driver = setup_chrome_driver()
    try:
        # Flaskアプリケーションのログインページにアクセス
        driver.get('http://localhost:5000/login')
        time.sleep(2)  # ページの読み込みを待つ
        
        # スクリーンショットの保存先ディレクトリを作成
        os.makedirs('doc/image', exist_ok=True)
        
        # スクリーンショットを保存
        driver.save_screenshot('doc/image/login.png')
        print("ログイン画面のスクリーンショットを保存しました。")
    
    finally:
        driver.quit()

if __name__ == '__main__':
    capture_login_page() 