#!/bin/bash

# Test Native Application Script

echo "Testing GymPro Native Application..."

# Mount DMG
DMG_FILE="target/dist/GymPro-1.0.0.dmg"
MOUNT_POINT="/tmp/gympro_test"

if [ ! -f "$DMG_FILE" ]; then
    echo "❌ DMG file not found: $DMG_FILE"
    echo "Please run: ./build-app.sh first"
    exit 1
fi

echo "Mounting DMG..."
hdiutil attach "$DMG_FILE" -mountpoint "$MOUNT_POINT" -quiet

if [ -d "$MOUNT_POINT/GymPro.app" ]; then
    echo "✅ App found in DMG"
    APP_PATH="$MOUNT_POINT/GymPro.app"
    
    echo "Testing app launch..."
    echo "Running: $APP_PATH/Contents/MacOS/GymPro"
    
    # Try to run and capture output
    "$APP_PATH/Contents/MacOS/GymPro" 2>&1 &
    APP_PID=$!
    
    sleep 3
    
    # Check if process is still running
    if ps -p $APP_PID > /dev/null; then
        echo "✅ App is running (PID: $APP_PID)"
        echo "App should be visible on screen"
        read -p "Press Enter to stop the app..."
        kill $APP_PID 2>/dev/null
    else
        echo "❌ App exited immediately. Check Console.app for errors"
        echo "Or run manually: $APP_PATH/Contents/MacOS/GymPro"
    fi
else
    echo "❌ App not found in DMG"
    echo "Contents of DMG:"
    ls -la "$MOUNT_POINT"
fi

# Unmount
echo "Unmounting DMG..."
hdiutil detach "$MOUNT_POINT" -quiet

echo ""
echo "To test manually:"
echo "1. Double-click: $DMG_FILE"
echo "2. Drag GymPro.app to Applications"
echo "3. Run from Applications or: open -a GymPro"

