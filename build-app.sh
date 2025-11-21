#!/bin/bash

# Build Native Application Script for macOS/Linux

echo "Building GymPro Native Application..."

# Step 1: Clean and package (creates JAR with all dependencies)
echo "Step 1: Cleaning and packaging..."
mvn clean package

# Step 2: Build native application with jpackage
echo "Step 2: Building native application..."
mvn jpackage:jpackage

echo ""
echo "✅ Build complete! Native application is in: target/dist/"
echo ""
echo "macOS: target/dist/GymPro.app"
echo "Windows: target/dist/GymPro.exe"
echo "Linux: target/dist/GymPro.deb or GymPro.rpm"

