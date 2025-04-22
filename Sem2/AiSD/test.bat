@echo off
setlocal enabledelayedexpansion

echo Enter the path to the Python file (e.g., main.py):
set /p PYTHON_FILE=

if not exist "%PYTHON_FILE%" (
    echo File not found: %PYTHON_FILE%
    pause
    exit /b
)

echo Enter the path to the test directory (e.g., tests):
set /p TEST_DIR=

if not exist "%TEST_DIR%" (
    echo Directory not found: %TEST_DIR%
    pause
    exit /b
)

echo.
echo Running tests from %TEST_DIR%
echo --------------------------------

for %%f in ("%TEST_DIR%\*.in") do (
    set "IN_FILE=%%~f"
    set "BASE=%%~nf"
    set "OUT_FILE=%TEST_DIR%\!BASE!.out"
    set "TEMP_FILE=__temp_output.txt"
    set "ERROR_FILE=__error_log.txt"

    echo Running test: !BASE!

    >nul 2>"!ERROR_FILE!" (
        python "%PYTHON_FILE%" < "!IN_FILE!" > "!TEMP_FILE!"
    )

    if errorlevel 1 (
        echo 💥 Test !BASE!: Exception occurred!
        type "!ERROR_FILE!"
        echo.
        del "!TEMP_FILE!" >nul 2>&1
        del "!ERROR_FILE!" >nul 2>&1
        goto :continue
    )

    if not exist "!OUT_FILE!" (
        echo ❓ Test !BASE!: Missing output file "!OUT_FILE!"
        goto :continue
    )

    fc /N "!OUT_FILE!" "!TEMP_FILE!" > "__diff.txt"
    if errorlevel 1 (
        echo ❌ Test !BASE!: Failed
        for /f "tokens=1,* delims=:" %%a in ('findstr /n .* "__diff.txt"') do (
            echo Line %%a: %%b
            goto :continue
        )
    ) else (
        echo ✅ Test !BASE!: Passed
    )

    :continue
    echo --------------------------------
    del "!TEMP_FILE!" >nul 2>&1
    del "!ERROR_FILE!" >nul 2>&1
    del "__diff.txt" >nul 2>&1
)

pause
