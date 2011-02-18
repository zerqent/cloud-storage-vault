#!/bin/bash
function usage {
    echo "Usage:"
    echo "  $0 <file.svg>"
    echo "Arguments:"
    echo "  -h: This help."
    echo "Note: The SVG has to have a transparent background to be autocropped."
}
if [[ -z "$@" ]]; then
    echo >&2 "You must supply an SVG file"
    usage
    exit 1
elif [[ "$@" = "-h" ]]; then
    usage
    exit 0
elif [[ ! -f "$@" ]]; then
    echo >&2 "$@ is not a file"
    usage
    exit 1
fi

if [ ! -f "`type -P inkscape`" ]
then
    echo "Could not find inkscape. Try:"
    echo "sudo aptitude install inkscape"
    exit 1
fi

filename=$(echo $1 | sed 's/\.svg//')

inkscape -D -z --file=$1 --verb=FitCanvasToSelectionOrDrawing --export-pdf=$filename.pdf
