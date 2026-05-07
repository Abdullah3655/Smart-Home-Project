"""Convert a markdown file to a styled standalone HTML rendered as a
'pattern-language codex' — a homage to Christopher Alexander's
*A Pattern Language* (1977), the book that gave the Gang of Four the
term 'design pattern'.

Usage:  python md2html_pro.py <input.md> <output.html>
"""
import sys
import pathlib
import markdown

CSS = r"""
/* ==================================================================
   PATTERN LANGUAGE CODEX
   Form mirrors content: a design-patterns report styled as a chapter
   from A Pattern Language. Cream rag paper, vermilion rubrication,
   EB Garamond, narrow book measure, three-star confidence ratings.
================================================================== */

:root {
  --paper:        #F1EAD8;
  --paper-warm:   #F8F1DD;
  --paper-deep:   #E7DEC4;
  --desk:         #2A211A;
  --ink:          #1A1611;
  --ink-soft:     #45382A;
  --muted:        #8B7857;
  --accent:       #A93226;   /* vermilion / cinnabar — manuscript rubric */
  --accent-deep:  #6F1D14;
  --accent-tint:  #E8C9C2;
  --rule:         #C8B79A;
  --rule-soft:    #E2D5B7;
}

@page { size: A4; margin: 22mm 24mm 24mm 24mm; }

* { box-sizing: border-box; }

@media print {
  *, *::before, *::after {
    -webkit-print-color-adjust: exact;
    print-color-adjust: exact;
    color-adjust: exact;
  }
  html, body { background: var(--paper) !important; }
  body { max-width: none !important; margin: 0 !important; padding: 0 !important; box-shadow: none !important; }
}

/* On-screen: paper floats on a darker desk */
@media screen {
  html {
    background:
      radial-gradient(ellipse at 50% 30%, #3A2E22 0%, #1F1812 100%);
    min-height: 100vh;
    padding: 32px 0 100px 0;
  }
  body {
    box-shadow:
      0 1px 0 rgba(255,235,200,.05),
      0 36px 90px -20px rgba(0,0,0,.7),
      0 12px 30px -10px rgba(0,0,0,.5);
  }
  /* Subtle laid-paper grain */
  body {
    background-image:
      url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='280' height='280' viewBox='0 0 280 280'><filter id='n'><feTurbulence type='fractalNoise' baseFrequency='0.85' numOctaves='2' stitchTiles='stitch'/><feColorMatrix values='0 0 0 0 0.10  0 0 0 0 0.07  0 0 0 0 0.04  0 0 0 0.07 0'/></filter><rect width='100%25' height='100%25' filter='url(%23n)'/></svg>");
    background-size: 280px 280px;
    background-attachment: local;
  }
}

html { background: var(--paper); }

body {
  font-family: 'EB Garamond', 'Iowan Old Style', Georgia, 'Adobe Garamond Pro', serif;
  font-feature-settings: 'liga', 'kern', 'onum', 'dlig';
  font-size: 11.5pt;
  line-height: 1.6;
  color: var(--ink);
  background: var(--paper);
  max-width: 16.6cm;        /* narrow book measure */
  margin: 0 auto;
  padding: 26mm 28mm 32mm 28mm;
  hyphens: auto;
  -webkit-hyphens: auto;
  text-rendering: optimizeLegibility;
  hanging-punctuation: first last allow-end;
}

/* ==================================================================
   TITLE PAGE — sparse, ceremonial
================================================================== */

h1 {
  font-family: 'EB Garamond', serif;
  font-size: 38pt;
  font-style: italic;
  font-weight: 400;
  line-height: 1.05;
  text-align: center;
  margin: 38mm 0 18pt 0;
  padding: 0;
  letter-spacing: -0.005em;
  text-wrap: balance;
}

h1::before {
  content: "BOOK \00B7 XII \00B7\A Patterns for the Smart Home";
  white-space: pre-line;
  display: block;
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 8.5pt;
  font-style: normal;
  font-weight: 500;
  letter-spacing: 0.32em;
  text-transform: uppercase;
  color: var(--accent);
  text-align: center;
  margin: 0 0 28pt 0;
  padding: 14pt 0;
  border-top: 1.5pt solid var(--ink);
  border-bottom: 0.5pt solid var(--ink);
  line-height: 1.7;
}

h1::after {
  content: "\2766";   /* floral fleuron */
  display: block;
  text-align: center;
  font-size: 22pt;
  color: var(--accent);
  font-style: normal;
  font-feature-settings: normal;
  margin: 22pt 0 0 0;
}

/* Colophon line — the date / submission / repo */
h1 + p {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  text-align: center;
  font-size: 8.5pt;
  letter-spacing: 0.06em;
  color: var(--ink-soft);
  margin: 0 0 50mm 0;     /* push the body to the next visual page */
  padding: 0;
  border: none;
}
h1 + p strong {
  display: inline;
  letter-spacing: 0.2em;
  text-transform: uppercase;
  color: var(--ink);
  font-weight: 600;
}
h1 + p a {
  color: var(--accent-deep);
  text-decoration: underline;
  text-underline-offset: 2px;
  text-decoration-thickness: 0.4pt;
}
h1 + p + hr { display: none; }

/* ==================================================================
   PATTERN ENTRIES — H2 sections styled as Alexander entries
================================================================== */

h2 {
  font-family: 'EB Garamond', serif;
  font-style: italic;
  font-weight: 400;
  font-size: 24pt;
  line-height: 1.15;
  text-align: center;
  margin: 32pt 0 4pt 0;
  padding: 18pt 0 0 0;
  border-top: 0.5pt solid var(--ink);
  text-wrap: balance;
  break-after: avoid;
  page-break-after: avoid;
  letter-spacing: 0.005em;
}

/* Three-star confidence rating after every pattern entry — Alexander's
   own grading system, used here for visual rhythm */
h2::after {
  content: "\2605 \00A0 \2605 \00A0 \2605";
  display: block;
  text-align: center;
  font-style: normal;
  font-size: 9pt;
  letter-spacing: 0.6em;
  color: var(--accent);
  margin: 14pt 0 18pt 0;
  padding-left: 0.6em;
  font-feature-settings: normal;
}

h3 {
  font-family: 'EB Garamond', serif;
  font-style: italic;
  font-weight: 400;
  font-size: 14pt;
  line-height: 1.25;
  text-align: center;
  color: var(--ink);
  margin: 18pt 0 6pt 0;
  break-after: avoid;
}

h4 {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 7.5pt;
  font-weight: 600;
  letter-spacing: 0.24em;
  text-transform: uppercase;
  color: var(--accent);
  text-align: center;
  margin: 14pt 0 6pt 0;
  break-after: avoid;
}

/* Drop cap on the first paragraph of section 1 — illuminated initial */
h2:first-of-type + p::first-letter {
  font-family: 'EB Garamond', serif;
  font-size: 5.4em;
  float: left;
  line-height: 0.82;
  margin: 6pt 10pt -4pt 0;
  color: var(--accent);
  font-weight: 400;
  font-style: normal;
}

/* First line of every section opener in small caps tracked out */
h2 + p::first-line {
  font-variant-caps: all-small-caps;
  letter-spacing: 0.08em;
  font-weight: 500;
  color: var(--ink);
}

/* ==================================================================
   BODY TEXT — justified, hyphenated, book-like
================================================================== */

p {
  margin: 0 0 7pt 0;
  text-align: justify;
  text-justify: inter-word;
  orphans: 3;
  widows: 3;
}
p strong { font-weight: 600; color: var(--ink); }
p em     { font-style: italic; color: var(--ink-soft); }

a {
  color: var(--accent-deep);
  text-decoration: underline;
  text-underline-offset: 2px;
  text-decoration-thickness: 0.4pt;
}
a:hover { color: var(--accent); }

/* Lists with old-style numerals + manicule-like markers */
ul, ol { margin: 6pt 0 8pt 0; padding-left: 1.6em; }
li { margin: 2pt 0; }
ul li::marker {
  content: "\276F  ";       /* heavy right-pointing angle */
  color: var(--accent);
}
ol li::marker {
  color: var(--accent);
  font-variant-numeric: oldstyle-nums;
  font-weight: 600;
}

/* ==================================================================
   CODE — typeset in vermilion, no boxes for inline
================================================================== */

code {
  font-family: 'JetBrains Mono', ui-monospace, 'SFMono-Regular', Consolas, monospace;
  font-size: 0.82em;
  color: var(--accent-deep);
  letter-spacing: -0.005em;
  background: transparent;
  padding: 0;
  border-radius: 0;
  overflow-wrap: anywhere;
  word-break: break-word;
  hyphens: manual;
}
code::before, code::after { content: "\200B"; }

pre {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 8.5pt;
  line-height: 1.6;
  color: var(--ink);
  background: var(--paper-warm);
  border: 0.5pt solid var(--rule);
  border-left: 2pt solid var(--accent);
  padding: 12pt 16pt;
  margin: 12pt 0;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
  word-break: break-word;
  break-inside: avoid;
}
pre code {
  color: var(--ink);
  font-size: inherit;
  overflow-wrap: anywhere;
  word-break: break-word;
}
pre code::before, pre code::after { content: none; }

/* ==================================================================
   TABLES — two species (image grid vs. data)
================================================================== */

table {
  border-collapse: collapse;
  margin: 12pt 0;
  width: 100%;
  font-size: 10pt;
  break-inside: avoid;
}

/* Layout (image) tables — borderless, generous padding */
table:has(img) td {
  text-align: center;
  padding: 14pt 8pt 16pt 8pt;
  border: none;
  vertical-align: top;
}
table:has(img) td b {
  display: inline-block;
  font-family: 'EB Garamond', serif;
  font-style: italic;
  font-weight: 400;
  font-size: 12pt;
  color: var(--ink);
  margin: 8pt 0 2pt 0;
}
table:has(img) img {
  border: 0.5pt solid var(--rule);
  background: var(--paper);
  padding: 0;
  display: inline-block;
  max-width: 100%;
  height: auto;
  box-shadow: 0 1pt 4pt rgba(26,22,17,.12);
}

/* Data tables — Alexander-style: rules at top + bottom only, small-caps headers */
table:has(th) {
  border-top: 1pt solid var(--ink);
  border-bottom: 1pt solid var(--ink);
}
table:has(th) th {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 7.5pt;
  font-weight: 600;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--ink);
  text-align: left;
  padding: 8pt 12pt 6pt 0;
  border-bottom: 0.5pt solid var(--ink);
  vertical-align: bottom;
  background: transparent;
}
table:has(th) th:last-child,
table:has(th) td:last-child { padding-right: 0; }
table:has(th) td {
  padding: 7pt 12pt 7pt 0;
  vertical-align: top;
  border-bottom: 0.25pt solid var(--rule-soft);
}
table:has(th) tr:last-child td { border-bottom: none; }
table:has(th) td:first-child {
  font-style: italic;
}
table:has(th) td:first-child strong {
  font-weight: 600;
  color: var(--accent-deep);
  font-style: normal;
}

/* ==================================================================
   BLOCKQUOTE — problem-statement / pull-quote
================================================================== */

blockquote {
  margin: 14pt 12pt;
  padding: 4pt 0 4pt 18pt;
  border-left: 1.5pt solid var(--accent);
  font-family: 'EB Garamond', serif;
  font-style: italic;
  font-size: 12.5pt;
  line-height: 1.5;
  color: var(--ink-soft);
  break-inside: avoid;
}
blockquote p { margin: 4pt 0; text-align: left; }

/* ==================================================================
   IMAGES & FLEURONS
================================================================== */

img { max-width: 100%; height: auto; }

p > img, p > a > img {
  display: block;
  margin: 12pt auto;
  border: 0.5pt solid var(--rule);
  background: var(--paper);
  box-shadow: 0 1pt 3pt rgba(26,22,17,.10);
}

/* Horizontal rule — floral fleuron asterism */
hr {
  border: none;
  text-align: center;
  margin: 24pt 0;
  height: auto;
  overflow: visible;
  page-break-after: avoid;
}
hr::before {
  content: "\00B7  \2766  \00B7";   /* · ❦ · */
  font-family: 'EB Garamond', serif;
  font-style: normal;
  font-feature-settings: normal;
  font-size: 16pt;
  color: var(--accent);
  letter-spacing: 0.6em;
  display: inline-block;
  padding-left: 0.6em;
}

/* ==================================================================
   PRINT NICETIES
================================================================== */

h2, h3, h4, figure, img, blockquote, pre, table { break-inside: avoid; }
h1, h2, h3, h4 { page-break-after: avoid; }
"""

FONT_LINK = (
    '<link rel="preconnect" href="https://fonts.googleapis.com">'
    '<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>'
    '<link href="https://fonts.googleapis.com/css2?'
    'family=EB+Garamond:ital,wght@0,400;0,500;0,600;1,400;1,500&'
    'family=JetBrains+Mono:wght@400;500;600&display=swap" rel="stylesheet">'
)

TEMPLATE = """<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8"/>
<title>{title}</title>
{fonts}
<style>{css}</style>
</head>
<body>
{body}
</body>
</html>
"""

def main(src: str, dst: str) -> None:
    src_path = pathlib.Path(src)
    md_text = src_path.read_text(encoding="utf-8")
    body = markdown.markdown(
        md_text,
        extensions=["tables", "fenced_code", "toc", "md_in_html", "attr_list"],
    )
    title = src_path.stem
    html = TEMPLATE.format(title=title, fonts=FONT_LINK, css=CSS, body=body)
    pathlib.Path(dst).write_text(html, encoding="utf-8")
    print(f"Wrote {dst} ({len(html):,} bytes)")

if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
