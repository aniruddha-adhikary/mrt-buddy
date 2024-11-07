#!/bin/bash
npm run build
rm -rf docs
mv out docs
