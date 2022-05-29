package fr.umontpellier.iut.graphes;
import java.util.ArrayList;
import java.util.SortedSet;

public class Graphe {
	/**
	 * matrice d'adjacence du graphe, un entier supérieur à 0 représentant la distance entre deux sommets
	 * mat[i][i] = 0 pour tout i parce que le graphe n'a pas de boucle
	 */
	private final int[][] mat;

	/**
	 * Construit un graphe à n sommets
	 *
	 * @param n le nombre de sommets du graphe
	 */
	public Graphe(int n) {

		mat = new int[n][n];
		for (int i = 0; i < n; i++) {

			for (int j = 0; j < n; j++) {

				mat[i][j] = 0;
			}
		}
	}

	public Graphe(int[][] mat) { //Fait par Quentin (pour estUnIsthme())

		this.mat = mat;
	}

	/**
	 * @return le nombre de sommets
	 */
	public int nbSommets() {
		return mat.length;
	}

	/**
	 * Supprime l'arête entre les sommets i et j
	 *
	 * @param i un entier représentant un sommet
	 * @param j un autre entier représentant un sommet
	 */
	public void supprimerArete(int i, int j) {
		mat[i][j] = 0;
		mat[j][i] = 0;
	}

	/**
	 * @param i un entier représentant un sommet
	 * @param j un autre entier représentant un sommet
	 * @param k la distance entre i et j (k>0)
	 */
	public void ajouterArete(int i, int j, int k) {

		mat[i][j] = k;
		mat[j][i] = k;
	}

	/**
	 * @return le nombre d'arête du graphe
	 */
	public int nbAretes() {

		int nbAretes = 0;

		for (int i = 0; i < mat.length; i++) {

			for (int j = i + 1; j < mat.length; j++) {

				if (mat[i][j] > 0) {

					nbAretes++;
				}
			}
		}
		return nbAretes;
	}

	/**
	 * @param i un entier représentant un sommet
	 * @param j un autre entier représentant un sommet
	 * @return vrai s'il existe une arête entre i et j, faux sinon
	 */
	public boolean existeArete(int i, int j) {  //Fait par Quentin
		return mat[i][j] > 0;
	}

	/**
	 * @param v un entier représentant un sommet du graphe
	 * @return la liste des sommets voisins de v
	 */
	public ArrayList<Integer> voisins(int v) { //Fait par Quentin

		ArrayList<Integer> voisins = new ArrayList<>();

		for (int i = 0; i < mat.length; i++) {

			//On parcours la ligne v du tableau, rajoutant la colonne i quand on croise une valeur supérieur à 0
			if (existeArete(v, i)) voisins.add(i);
		}
		return voisins;
	}

	/**
	 * @return une chaîne de caractères permettant d'afficher la matrice mat
	 */
	public String toString() {
		StringBuilder res = new StringBuilder("\n");
		for (int[] ligne : mat) {
			for (int j = 0; j < mat.length; j++) {
				String x = String.valueOf(ligne[j]);
				res.append(x);
			}
			res.append("\n");
		}
		return res.toString();
	}

	/**
	 * Calcule la classe de connexité du sommet v
	 *
	 * @param v un entier représentant un sommet
	 * @return une liste d'entiers représentant les sommets de la classe de connexité de v
	 */
	public ArrayList<Integer> calculerClasseDeConnexite(int v) { //Fait par Quentin

		int sommet; //Sommet qui sera traité dans la boucle
		ArrayList<Integer> classe = new ArrayList<>(); //Classe de connexitté à retourner
		ArrayList<Integer> bleu = new ArrayList<>(); //Liste des sommets dont on a pas encore calculé les voisins
		bleu.add(v);

		while (!bleu.isEmpty()) {

			sommet = bleu.remove(0);
			bleu.addAll(voisins(sommet));
			bleu.removeAll(classe);
			classe.add(sommet);
		}
		return classe;
	}

	/**
	 * @return la liste des classes de connexité du graphe
	 */
	public ArrayList<ArrayList<Integer>> calculerClassesDeConnexite() throws IndexOutOfBoundsException { //Fait par Quentin

		ArrayList<ArrayList<Integer>> classes = new ArrayList<>(); //Liste de classe de connexité à retourner

		try { //Au cas où la matrice du graphe est vide

			classes.add(new ArrayList<>(calculerClasseDeConnexite(0)));
		}
		catch (IndexOutOfBoundsException e) {

			throw new IndexOutOfBoundsException("Le graphe ne comporte pas de sommet");
		}

		for (int sommet = 1; sommet < mat.length; sommet++) { //Commence à 1 car la classe de 0 est déjà dans classes

			if (!estContenu(classes, sommet)) {

				classes.add(new ArrayList<>(calculerClasseDeConnexite(sommet))); //Ajoute la classe de connexité
			}
		}
		return classes;
	}

	public boolean estContenu(ArrayList<ArrayList<Integer>> classes, int sommet) { //Fait par Quentin (pour calculerClassesDeConnexite())

		for (int i = 0; i < classes.size(); i++) { //On test toutes les classes existant pour le moment

			if (classes.get(i).contains((sommet))) { //Si la classe[i] contient le sommet

				return true;
			}
		}
		return false;
	}

	/**
	 * @return le nombre de classes de connexité
	 */
	public int nbCC() {
		return calculerClassesDeConnexite().size();
	}

	/**
	 * @param u un entier représentant un sommet
	 * @param v un entie représentant un sommet
	 * @return vrai si (u,v) est un isthme, faux sinon
	 */
	public boolean estUnIsthme(int u, int v) { //Fait par Quentin

		Graphe graphe = new Graphe(copy());

		graphe.supprimerArete(u, v);

		return graphe.nbCC() > this.nbCC(); //Si l'arête supprimée a créé une nouvelle classe de connexité
	}

	public int[][] copy() { //Fait par Quentin (pour Graphe(int[][] mat))

		int[][] mat = new int[this.mat.length][this.mat.length];

		for (int i = 0; i < mat.length; i++) {

			for (int j = 0; j < mat.length; j++) {

				mat[i][j] = this.mat[i][j];
			}
		}
		return mat;
	}

	/**
	 * Calcule le plus long chemin présent dans le graphe
	 * @return une liste de sommets formant le plus long chemin dans le graphe
	 */
	public ArrayList<Integer> plusLongChemin() throws IndexOutOfBoundsException {

		Graphe graphe;
		ArrayList<ArrayList<Integer>> chemins = new ArrayList<>();
		ArrayList<Integer> voisins;
		ArrayList<Integer> biggestClasse = new ArrayList<>();
		int sommet = 0;
		int precedent;

		try { //Au cas où la matrice du graphe est vide

			biggestClasse.addAll(calculerClasseDeConnexite(0));
		}
		catch (IndexOutOfBoundsException e) {

			throw new IndexOutOfBoundsException("Le graphe ne comporte pas de sommet");
		}

		if (existeParcoursEulerien()) { //Si c'est un parcours Eulerien, alors on peut passer par toutes les arêtes du graphe

			for (int i = 0; i < mat.length; i++) { //Parcours tous les sommets du graphe

				if (voisins(i).size() == 1) { //Si on trouve un sommet de degré 1, on doit commencer par celui-ci, sinon le sommet 0

					sommet = i;
					break;
				}
			}
			do {

				graphe = new Graphe(copy());
				chemins.add(new ArrayList<>());
				for (int i = 0; i < nbAretes(); i++) {

					voisins = voisins(sommet); //On génère les voisins du sommet
					if (voisins.isEmpty()) break; //S'il n'y a pas de voisins, on refait un nouveau chemin
					sommet = voisins.get(0); //Il faut regarder que le prochain

					precedent = chemins.get(chemins.size() - 1).get(chemins.size() - 1);
					graphe.supprimerArete(precedent, sommet);
					chemins.get(chemins.size() - 1).add(sommet);
				}
			}
			while (chemins.get(chemins.size() - 1).size() != nbAretes()) ;
			return chemins.get(chemins.size() - 1);
		}
		else { //Sinon on va trouver la plus grosse classe de connexité du graphe

			for (int i = 1; i < nbCC(); i++) { //Commence à 1 car la classe de connexité du sommet 0 est déjà dans biggestClasse

				if (biggestClasse.size() < calculerClassesDeConnexite().get(i).size()) { //Si la taille de la classe actuelle est plus grande que l'ancienne

					biggestClasse = calculerClassesDeConnexite().get(i); //Mettre la classe de connexité actuelle dans biggestClasse
				}
			}
		}
		return null;
	}

	public ArrayList<Integer> wrongPath(ArrayList<ArrayList<Integer>> p_chemins) {

		ArrayList<ArrayList<Integer>> chemins = new ArrayList<>(p_chemins);
		ArrayList<Integer> chemin = new ArrayList<>(chemins.get(chemins.size() - 1));



		//for ()
		return null;
	}

	/**
	 * Ajout de Quentin:
	 * <p>D'après le théorème d'Euler : Un graphe connexe admet une chaîne eulérienne si et seulement s'il possède zéro ou deux sommet(s) de degré impair. Un graphe connexe admet un cycle eulérien si et seulement s'il ne possède que des sommets de degré pair.
	 * @return vrai s'il existe un parcours eulérien dans le graphe, faux sinon
	 */
	public boolean existeParcoursEulerien() { //Fait par Quentin (je suis pas trop sûr d'avoir correctement appliqué)

		int oddCpt = 0; //Compte le nombre de sommet d'ordre impair, retourne vrai seulement si oddCpt ⊆ {0,2}

		if (nbCC() == 1) {

			for (int i = 0; i < mat.length; i++) {

				if (voisins(i).size() % 2 == 1) oddCpt++;
			}
		}
		else return false;

		return oddCpt == 0 || oddCpt == 2;
	}

	/**
	 * @return vrai si le graphe est un arbre, faux sinon
	 */
	public boolean estUnArbre() { //Fait par Quentin

		return nbCC() == 1 && nbAretes() == nbSommets() - 1; //Graphe connexe et longueur de n-1
	}
}