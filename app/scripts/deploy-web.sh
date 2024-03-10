#!/bin/sh

set -e

cd $(dirname "$0")/..
PROJECT_PATH="$(pwd)"

TARGET_PATH="$PROJECT_PATH/src/main/assets/web"
rm -rf $TARGET_PATH

cd "$PROJECT_PATH/web"
if [ ! -d './node_modules' ]; then
  echo "Execute 'npm install'..."
  npm install
fi
rm -rf ./dist
npm run build

mkdir "$TARGET_PATH"
touch "$TARGET_PATH/.gitkeep"
cp -rf ./dist/* "$TARGET_PATH/"