###############################################################################
#
# Tahoe-LAFS with SHA-3 Candidates Paper Makefile
#
###############################################################################

# compiler

PDFLATEX = pdflatex

###############################################################################

main.pdf: main.tex *.bib tables/*.tex listings/*.*
	pdflatex main.tex
	bibtex main
	pdflatex main.tex
	pdflatex main.tex
	rm -rf *.aux *.log *.toc *.out *.lof *.lot *.bbl *.blg *.dvi *.lol

problem:
	pdflatex problem_description.tex
	pdflatex problem_description.tex
	rm -rf *.aux *.log *.toc *.out *.lof *.lot

presentation.pdf: presentation.tex
	pdflatex presentation.tex
	pdflatex presentation.tex
	rm -rf *.aux *.log *.toc *.out *.lof *.lot *.bbl *.blg *.dvi *.nav *.snm *.vrb

show:
	xdg-open main.pdf

all: main.pdf presentation.pdf

display: clean main.pdf show

snapshot: main.pdf
	cp main.pdf snapshots/report-snapshot-`date +%Y-%m-%d`.pdf

clean:
	rm -rf *.aux *.log *.toc *.out *.lof *.lot *.bbl *.blg *.dvi *.pdf *.lol

.PHONY: clean problem show
