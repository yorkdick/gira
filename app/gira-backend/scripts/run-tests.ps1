# 设置变量
$projectRoot = Split-Path -Parent $PSScriptRoot
$testResultsDir = "$projectRoot\target\test-results"
$coverageReportsDir = "$projectRoot\target\coverage-reports"
$testReportFile = "$projectRoot\doc\test-results.md"

# 清理旧的测试结果
Write-Host "Cleaning old test results..."
if (Test-Path $testResultsDir) {
    Remove-Item -Recurse -Force $testResultsDir
}
if (Test-Path $coverageReportsDir) {
    Remove-Item -Recurse -Force $coverageReportsDir
}

# 执行测试
Write-Host "Running tests..."
mvn clean test

# 检查测试是否成功
if ($LASTEXITCODE -eq 0) {
    Write-Host "Tests completed successfully, generating reports..."
    
    # 读取测试结果
    $testResults = Get-Content "$testResultsDir\TEST-*.xml" | Select-String -Pattern 'tests="(\d+)" skipped="(\d+)" failures="(\d+)" errors="(\d+)"'
    $totalTests = 0
    $totalSkipped = 0
    $totalFailures = 0
    $totalErrors = 0
    
    foreach ($result in $testResults) {
        $totalTests += [int]$result.Matches.Groups[1].Value
        $totalSkipped += [int]$result.Matches.Groups[2].Value
        $totalFailures += [int]$result.Matches.Groups[3].Value
        $totalErrors += [int]$result.Matches.Groups[4].Value
    }
    
    $passedTests = $totalTests - $totalSkipped - $totalFailures - $totalErrors
    $passRate = [math]::Round(($passedTests / $totalTests) * 100, 2)
    
    # 读取覆盖率报告
    $coverageReport = Get-Content "$coverageReportsDir\index.html" | Select-String -Pattern 'Total.*?([0-9.]+)%'
    $coverage = $coverageReport.Matches.Groups[1].Value
    
    # 生成测试报告
    $testReport = @"
# GIRA Backend Test Results

## 1. Test Overview

- Test Time: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
- Environment: Development
- Scope: Full Test Suite
- Executor: Automated Test

## 2. Test Statistics

### 2.1 Overall Statistics
- Total Test Cases: $totalTests
- Passed: $passedTests
- Failed: $totalFailures
- Errors: $totalErrors
- Skipped: $totalSkipped
- Pass Rate: $passRate%
- Code Coverage: $coverage%

### 2.2 Detailed Results

Detailed test results and coverage reports can be found at:
- Test Report: $testResultsDir
- Coverage Report: $coverageReportsDir\index.html

## 3. Failed Test Analysis

"@

    # 如果有失败的测试，添加失败详情
    if ($totalFailures -gt 0 -or $totalErrors -gt 0) {
        $failureDetails = Get-Content "$testResultsDir\TEST-*.xml" | Select-String -Pattern '<failure.*?message="(.*?)".*?>'
        $testReport += "`n### 3.1 Failure Details`n"
        foreach ($failure in $failureDetails) {
            $testReport += "- $($failure.Matches.Groups[1].Value)`n"
        }
    }

    # 保存测试报告
    $testReport | Out-File -FilePath $testReportFile -Encoding UTF8
    
    Write-Host "Test report generated: $testReportFile"
    Write-Host "Coverage report: $coverageReportsDir\index.html"
} else {
    Write-Host "Test execution failed!"
    exit 1
} 