#!/bin/bash
git status
git status --porcelain|awk '{if($1=="??") {print "git add " $2}}'
git fetch origin
git merge origin/master