"""Convert a markdown file to a styled standalone HTML ready for browser
print-to-PDF.

Usage:  python md2html.py <input.md> <output.html>
"""
import sys
import pathlib
import markdown

CSS = r"""
/* ------------------------------------------------------------------
   "Studies in Software Design Patterns" — archival editorial style
   Warm cream paper · deep ink · burnt amber accents
------------------------------------------------------------------ */

:root {
  --paper:        #F5EFE3;   /* warm cream */
  --paper-tint:   #FAF6EC;
  --paper-deep:   #ECE4D2;
  --desk:         #2E261C;   /* dark warm taupe (off-page) */
  --ink:          #1A1611;
  --ink-soft:     #4A4137;
  --muted:        #8A7E6B;
  --accent:       #A4621D;   /* burnt sienna — print-friendly amber */
  --accent-deep:  #6E3F0E;
  --accent-tint:  #E9D7B8;
  --teal:         #0C4A45;
  --rule:         #C8BCA4;
  --rule-soft:    #E2D8C2;
}

@page { size: A4; margin: 18mm 18mm 20mm 18mm; }

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

/* On-screen: paper floats on a warm desk-tone background */
@media screen {
  html {
    background: var(--desk);
    min-height: 100vh;
    padding: 28px 0 80px 0;
  }
  body {
    box-shadow: 0 1px 0 rgba(255,240,210,.04), 0 24px 60px -10px rgba(0,0,0,.55);
  }
}

html { background: var(--paper); }

body {
  font-family: 'Newsreader', 'Source Serif Pro', Georgia, 'Iowan Old Style', serif;
  font-feature-settings: 'liga', 'kern', 'onum';
  font-variation-settings: 'opsz' 14;
  font-size: 10.5pt;
  line-height: 1.58;
  color: var(--ink);
  background: var(--paper);
  max-width: 20.4cm;
  margin: 0 auto;
  padding: 22mm 18mm 26mm 18mm;
  text-rendering: optimizeLegibility;
  hanging-punctuation: first last allow-end;
  hyphens: auto;
  -webkit-hyphens: auto;
  overflow-wrap: break-word;
  word-wrap: break-word;
}

/* Subtle paper grain — screen only */
@media screen {
  body {
    background-image:
      url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='240' height='240' viewBox='0 0 240 240'><filter id='n'><feTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='2' stitchTiles='stitch'/><feColorMatrix values='0 0 0 0 0.10  0 0 0 0 0.08  0 0 0 0 0.05  0 0 0 0.05 0'/></filter><rect width='100%25' height='100%25' filter='url(%23n)'/></svg>");
    background-size: 240px 240px;
    background-attachment: local;
  }
}

/* ------------------------------------------------------------------
   Masthead — eyebrow + title + colophon line
------------------------------------------------------------------ */

h1 {
  font-family: 'Newsreader', Georgia, serif;
  font-variation-settings: 'opsz' 72, 'wght' 500;
  font-weight: 500;
  font-size: 34pt;
  line-height: 1.02;
  letter-spacing: -0.012em;
  color: var(--ink);
  margin: 0 0 10pt 0;
  padding: 0;
  text-wrap: balance;
}

h1::before {
  content: "✻  Studies in Software Design Patterns  ✻";
  display: block;
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 8pt;
  font-weight: 500;
  letter-spacing: 0.32em;
  text-transform: uppercase;
  color: var(--accent);
  margin: 0 0 16pt 0;
  padding: 12pt 0 10pt 0;
  border-top: 2pt solid var(--ink);
  border-bottom: 0.5pt solid var(--ink);
  text-align: center;
}

/* The first paragraph after H1 is a typeset colophon line */
h1 + p {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 8.5pt;
  line-height: 1.5;
  letter-spacing: 0.01em;
  color: var(--ink-soft);
  margin: 0 0 6pt 0;
  padding: 8pt 0 10pt 0;
  border-bottom: 0.5pt solid var(--rule);
}
h1 + p strong {
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.06em;
  text-transform: uppercase;
  font-size: 0.92em;
}
h1 + p a {
  color: var(--accent-deep);
  text-decoration: underline;
  text-underline-offset: 2px;
  text-decoration-thickness: 0.4pt;
}

/* The horizontal rule right after the colophon disappears (it'd be redundant) */
h1 + p + hr { display: none; }

/* ------------------------------------------------------------------
   Section headings
------------------------------------------------------------------ */

h2 {
  font-family: 'Newsreader', serif;
  font-variation-settings: 'opsz' 36, 'wght' 500;
  font-weight: 500;
  font-size: 18.5pt;
  line-height: 1.15;
  margin: 26pt 0 8pt 0;
  padding: 10pt 0 0 0;
  color: var(--ink);
  letter-spacing: -0.005em;
  border-top: 0.5pt solid var(--ink);
  break-after: avoid;
  text-wrap: balance;
}

h3 {
  font-family: 'Newsreader', serif;
  font-variation-settings: 'opsz' 18, 'wght' 500;
  font-weight: 500;
  font-style: italic;
  font-size: 13pt;
  line-height: 1.25;
  color: var(--ink);
  margin: 16pt 0 4pt 0;
  break-after: avoid;
}

h4 {
  font-family: 'JetBrains Mono', ui-monospace, monospace;
  font-size: 8.5pt;
  font-weight: 600;
  letter-spacing: 0.18em;
  text-transform: uppercase;
  color: var(--accent);
  margin: 12pt 0 4pt 0;
  break-after: avoid;
}

/* Drop cap on the first paragraph of the very first section */
h2:first-of-type + p::first-letter {
  font-family: 'Newsreader', serif;
  font-variation-settings: 'opsz' 96, 'wght' 500;
  font-size: 4.4em;
  float: left;
  line-height: 0.84;
  margin: 6pt 8pt -2pt 0;
  color: var(--accent);
  font-weight: 500;
}

/* Section openers: first line in small caps, tracked out — editorial flourish */
h2 + p::first-line {
  font-variant-caps: all-small-caps;
  letter-spacing: 0.06em;
  font-weight: 500;
  color: var(--ink);
}

/* ------------------------------------------------------------------
   Body text
------------------------------------------------------------------ */

p {
  margin: 0 0 7pt 0;
  orphans: 3;
  widows: 3;
}

p strong { font-weight: 600; color: var(--ink); }
p em { font-style: italic; color: var(--ink-soft); }

a {
  color: var(--accent-deep);
  text-decoration: underline;
  text-underline-offset: 2px;
  text-decoration-thickness: 0.4pt;
}
a:hover { color: var(--accent); }

ul, ol { margin: 6pt 0 8pt 0; padding-left: 1.4em; }
li { margin: 2pt 0; }
ul li::marker { color: var(--accent); }
ol li::marker { color: var(--accent); font-variant-numeric: oldstyle-nums; font-weight: 600; }

/* ------------------------------------------------------------------
   Code — inline code is *typeset*, not boxed
------------------------------------------------------------------ */

code {
  font-family: 'JetBrains Mono', ui-monospace, 'SFMono-Regular', Consolas, monospace;
  font-size: 0.86em;
  color: var(--accent-deep);
  letter-spacing: -0.005em;
  background: transparent;
  padding: 0;
  border-radius: 0;
  /* Long slash- or dot-separated identifier chains must break */
  overflow-wrap: anywhere;
  word-break: break-word;
  hyphens: manual;
}
/* Don't strand a single break-character on the next line */
code::before, code::after { content: "\200B"; }

/* Code blocks get a tinted card with an accent flag */
pre {
  font-family: 'JetBrains Mono', monospace;
  font-size: 8.5pt;
  line-height: 1.55;
  color: var(--ink);
  background: var(--paper-tint);
  border: 0.5pt solid var(--rule);
  border-left: 2pt solid var(--accent);
  padding: 10pt 14pt;
  margin: 10pt 0;
  overflow-x: auto;
  white-space: pre-wrap;
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

/* ------------------------------------------------------------------
   Tables — two species:
     • Layout tables (contain images): no borders, generous gutters
     • Data tables (have <th>): hairline rows, small-caps headers
------------------------------------------------------------------ */

table {
  border-collapse: collapse;
  margin: 10pt 0 12pt 0;
  width: 100%;
  break-inside: avoid;
  font-size: 9.5pt;
}

/* --- layout (image grid) tables --- */
table:has(img) { background: transparent; margin: 14pt 0; }
table:has(img) td {
  padding: 12pt 8pt 14pt 8pt;
  text-align: center;
  vertical-align: top;
  border: none;
}
table:has(img) td b {
  display: inline-block;
  font-family: 'Newsreader', serif;
  font-variation-settings: 'opsz' 24, 'wght' 500;
  font-style: italic;
  font-weight: 500;
  font-size: 11pt;
  color: var(--ink);
  margin: 8pt 0 2pt 0;
  letter-spacing: 0.005em;
}
table:has(img) img {
  border: 0.5pt solid var(--rule);
  background: var(--paper);
  padding: 0;
  display: inline-block;
  max-width: 100%;
  height: auto;
  box-shadow: 0 1pt 3pt rgba(26,22,17,.10);
}

/* --- data tables --- */
table:has(th) {
  border-top: 0.75pt solid var(--ink);
  border-bottom: 0.75pt solid var(--ink);
  margin: 12pt 0 14pt 0;
}
table:has(th) th {
  font-family: 'JetBrains Mono', monospace;
  font-size: 8pt;
  font-weight: 600;
  letter-spacing: 0.16em;
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
  font-variation-settings: 'opsz' 14, 'wght' 500;
}
table:has(th) td:first-child strong {
  font-weight: 600;
  color: var(--accent-deep);
}

/* ------------------------------------------------------------------
   Blockquote — pull-quote style
------------------------------------------------------------------ */

blockquote {
  margin: 12pt 0 12pt 0;
  padding: 4pt 0 4pt 16pt;
  border-left: 1.5pt solid var(--accent);
  font-family: 'Newsreader', serif;
  font-variation-settings: 'opsz' 18;
  font-style: italic;
  font-size: 11.5pt;
  line-height: 1.45;
  color: var(--ink-soft);
  break-inside: avoid;
}
blockquote p { margin: 4pt 0; }

/* ------------------------------------------------------------------
   Images & figures
------------------------------------------------------------------ */

img {
  max-width: 100%;
  height: auto;
}

/* Standalone images (not inside a layout table) get a hairline frame */
p > img,
p > a > img {
  border: 0.5pt solid var(--rule);
  background: var(--paper);
  box-shadow: 0 1pt 3pt rgba(26,22,17,.08);
}

/* ------------------------------------------------------------------
   Horizontal rule — ornamental fleuron asterism
------------------------------------------------------------------ */

hr {
  border: none;
  text-align: center;
  margin: 22pt 0;
  height: auto;
  overflow: visible;
  page-break-after: avoid;
}
hr::before {
  content: "✦  ✦  ✦";
  font-family: 'Newsreader', serif;
  font-style: italic;
  font-size: 11pt;
  color: var(--accent);
  letter-spacing: 0.7em;
  display: inline-block;
  padding-left: 0.7em;
}

/* ------------------------------------------------------------------
   Print niceties
------------------------------------------------------------------ */

h2, h3, h4, figure, img, blockquote, pre, table { break-inside: avoid; }
h1, h2, h3, h4 { page-break-after: avoid; }
"""

FONT_LINK = (
    '<link rel="preconnect" href="https://fonts.googleapis.com">'
    '<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>'
    '<link href="https://fonts.googleapis.com/css2?'
    'family=Newsreader:ital,opsz,wght@0,6..72,400;0,6..72,500;0,6..72,600;'
    '1,6..72,400;1,6..72,500&'
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
