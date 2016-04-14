# Application mobile d'aide au déplacement dans un centre commercial

## Contexte :

Un récent projet SIG mené par des élèves « Géomètre 2ème année » de l' ENSG a permis de créer un SIG du centre commercial
des Arcades modélisant les niveaux, les boutiques et services, ainsi qu’une grande partie du réseau piéton sous
forme d’une graphe. (Seuls les sens des escalators et la présence des ascenseurs n’ont pas été modélisés faute de
temps). Un prototype fonctionnel sous le SIG Arcmap a été produit.

Le but de ce projet est de poursuivre ce travail et de l’adapter à une utilisation mobile utilisant un
graphe de déplacement et une cartographe créés de toute pièce.


Il s’agira donc de développer une application mobile d’aide au déplacement dans ce centre
commercial destinée principalement aux clients et visiteurs, voire aux services de sécurité et de
secours.

## Objectif du projet :

- Compléter le graphe de déplacement piéton
- Créer une application mobile Android permettant :
o D’afficher une carte/plan de façon géoréférencée ou non
o De stocker une base de données géolocalisée contenant la position des points
d’intérêts du centre commercial (services, boutiques, commodités, etc.)
o De se positionner sans système GPS (ex : lire des QR-codes et les associer à une
position géographique connue)
o De calculer des itinéraires entre une position donnée, par exemple grâce au QRcodes,
et un point d’intérêt choisi.
o D’afficher le résultat du calcul sur la carte.
Et, si le temps vous le permet :
o De prendre en compte l’aspect multi-niveau du centre commercial.

## Intérêts :
- Application très pratique de la géomatique
- Variété des thématiques traitées (programmation, positionnement, cartographie, SIG,
topologie)
- Sujet très « dans l’air du temps »

## Difficultés pour le groupe :
- Technologie récente et parfois délicate à maîtriser
- Poursuite d’un travail passé
- Documentation des API pas toujours parfaite
- Etre capable de réagir très vite en cas de difficulté importante (forums, aide technique, etc.)

## Quelques technologies pouvant vous inspirer :
- API Google Maps
- SDK Arcgis Android
- SDK Graphhopper pour le calcul d’itinéraire