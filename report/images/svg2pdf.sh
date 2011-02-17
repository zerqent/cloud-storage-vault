#!/bin/bash

filename=$(echo $1 | sed 's/\.svg//')

inkscape -D -z --file=$1 --verb=FitCanvasToSelectionOrDrawing --export-pdf=$filename.pdf
