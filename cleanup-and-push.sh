#!/bin/bash

# Step 1: Delete all .jar and .war files from the project
echo "🧹 Cleaning all .jar and .war files..."
find . -type f \( -name "*.jar" -o -name "*.war" \) -exec rm -f {} +

# Step 2: Untrack deleted files from Git
echo "🔄 Removing them from Git cache..."
git rm --cached -r .

# Step 3: Setup .gitignore to skip all but Java files
echo "📄 Updating .gitignore..."
echo -e "*\n!*/\n!*.java" > .gitignore

# Step 4: Add only Java files
echo "➕ Adding only .java files..."
git add .

# Step 5: Commit
echo "✅ Committing changes..."
git commit -m "Clean push: only .java files (removed .jar/.war)"

# Step 6: Push to GitHub
echo "🚀 Pushing to remote repo..."
git push origin main --force

echo "🎉 Done! Only .java files are now in the repo."

