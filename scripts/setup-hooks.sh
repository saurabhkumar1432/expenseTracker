#!/bin/bash
# Shell script to set up git hooks on macOS/Linux
# Run this script once after cloning the repository

echo "🔧 Setting up git hooks..."

# Set the hooks directory
git config core.hooksPath .githooks

# Make hooks executable
chmod +x .githooks/*

if [ $? -eq 0 ]; then
    echo "✅ Git hooks configured successfully!"
    echo ""
    echo "The following hooks are now active:"
    echo "  • pre-commit  - Scans for secrets before each commit"
    echo "  • pre-push    - Final safety check before pushing"
    echo ""
    echo "To bypass hooks in emergencies: git commit --no-verify"
else
    echo "❌ Failed to configure git hooks"
    exit 1
fi
