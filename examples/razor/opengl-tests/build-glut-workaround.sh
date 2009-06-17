#!/bin/bash
gcc -std=c99 glut-workaround.c -oglut-workaround -I../lib -lGL -lGLU -lglut /usr/lib/libgc.a
