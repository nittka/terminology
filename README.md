# Lightweight terminology management
## Why terminology management
It is next to impossible to achieve consistent usage of terms across all phases of development (specification, implementation, marketing, user documentation etc.). This does not mean that you should not try!

A good strategy is extracting the concepts, collecting the names (possibly in multiple languages) agreed on for those concepts and making the result available to all parties involved. This is what terminology management is about.

## Spreadsheet is not enough, Term bases are too much
All information concerning one concept should be treated as a unit - definition, multiple terms in multiple languages, clarifying information about a term, such as

* status (is it the preferred term or one that must not be used anymore?)
* grammatical information
* hints as to typical usage; markers for customer or product specific usage

There is standard software to do all that in a sophisticated and highly customizable way, but term bases cost money, store the information in proprietary formats, and typically only a fraction of their functionality is needed. So often, a spreadsheet based approach is taken. However, this does not support concept oriented approach and is difficult to maintain across a team. Search and filter capabilities are present but not optimized for terminologies.

# Lightweight Xtext based terminology editor for Eclipse
The following feature list should convince you that you can do better:

* concept oriented structure
* text based, human readable, version controllable storage format
* code completion (including templates), highlighting, code folding, validation, outline and quick outline
* dedicated search dialog for terms (including search in definition and usage, as well as product/customer restriction)
* many of the features are customizable via the Eclipse preference page

However, not all parties interested in the terminology will want an Eclipse installation or need one. This is why there is an export extension point, as well as 4 example exporters for typical use cases

* csv (fallback / to provide word lists to translators)
* tbx (standard xml format for terminologies)
* wiki (paste simple word list to your company's wiki)
* json (raw data to provide a browser based search in your terminology)

The typical user does not want to edit the terminology but only researches terms (so if she is not already working in an Eclipse environment, she will not want to start one in order to do the research). This is why validation and exporters are disabled by default. The exporters are not part of the default installation but available via an optional feature. Many of the features for the terminology manager are customizable via the Eclipse preference page.

Adapt the example exporters to your needs or write a completely new one. Place it in the installation's dropins folder and it will be available (and configurable via preference page) immediately.

## Installation
You need an Eclipse with Xtext and the Terminology Editor installed.
The last Eclipse version supported by the original version of the Terminology plugin (1.2.0) is Eclipse 2021-12 (Xtext up to version 2.29).
For later Eclipse versions use the plugin version 2.0.0 (or later). Install

* an Xtext Redistributable/Xtext SDK from the [Xtext update site](http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/)
* the terminology editor (and possibly the exporter) feature from the [Terminology update site](https://raw.githubusercontent.com/nittka/terminology/master/de.itemis.tooling.terminology.updatesite/site.xml)

or use the [Oomph](https://wiki.eclipse.org/Eclipse_Oomph_Installer "Oomph Installer") setup files (add the following links to the user projects in the project catalog)

* [Terminology Editor User setup](https://raw.githubusercontent.com/nittka/terminology/master/terminologyUse.setup) for the editor or editor and exporters (depending on the branch you choose)
* [Terminology Editor Developer setup](https://raw.githubusercontent.com/nittka/terminology/master/terminologyDev.setup) for the complete source code (Choose an Eclipse IDE for Java and DSL Developers)

Import the `sample.terminology` and `terminology.websearch.scaffold` projects from the [Terminology Project archive](https://github.com/nittka/terminology/archive/master.zip) to get a brief overview of the syntax of the terminology files. When installing the editor via Oomph, the zip-file is automatically copied to your workspace location, so you can import the projects immediately from that archive.

## Known limitations
The Xtext terminology editor is not intended to be a full-blown term base. It will not be sufficient for terminologies with many languages - where you would need definitions on a per language basis.

You can mark entries as related, but you cannot further qualify what type of relation they have.

Feel free to file an [issue](https://github.com/nittka/terminology/issues), if you think that a common use case feature is missing.

... See the [Wiki](https://github.com/nittka/terminology/wiki "Terminology Wiki") for more information on any of the topics.
