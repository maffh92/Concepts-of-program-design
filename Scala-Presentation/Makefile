
default:newslides.pdf

verbose:newslides.md
	pandoc -t beamer newslides.md -o newslides.pdf --latex-engine=xelatex --verbose --template beamer-template.tex


newslides.pdf:newslides.md
	pandoc -t beamer newslides.md -o newslides.pdf --latex-engine=xelatex --template beamer-template.tex

