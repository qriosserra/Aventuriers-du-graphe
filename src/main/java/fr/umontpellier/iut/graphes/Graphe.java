package fr.umontpellier.iut.graphes;
import javax.xml.validation.SchemaFactoryLoader;
import java.util.ArrayList;
import java.util.Collections;

public class Graphe {
	/**
	 * matrice d'adjacence du graphe, un entier supérieur à 0 représentant la distance entre deux sommets
	 * mat[i][i] = 0 pour tout i parce que le graphe n'a pas de boucle
	 */
	private final int[][] mat;

	/**
	 * Construit un graphe à n sommets
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
	 * @param i un entier représentant un sommet
	 * @param j un autre entier représentant un sommet
	 */
	public Graphe supprimerArete(int i, int j) {
		mat[i][j] = 0;
		mat[j][i] = 0;
		return this;
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
			if (existeArete(v, i) && v != i) voisins.add(i);
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
	 * @param v un entier représentant un sommet
	 * @return une liste d'entiers représentant les sommets de la classe de connexité de v
	 */
	public ArrayList<Integer> calculerClasseDeConnexite(int v) {

		int sommet; //Sommet qui sera traité dans la boucle
		ArrayList<Integer> classe = new ArrayList<>(); //Classe de connexitté à retourner
		ArrayList<Integer> bleu = new ArrayList<>(); //Liste des sommets dont on a pas encore calculé les voisins
		bleu.add(v);

		while (!bleu.isEmpty()) {

			sommet = bleu.remove(0);
			classe.add(sommet);
			bleu.addAll(voisins(sommet));
			bleu.removeAll(classe);
		}
		return classe;
	}

	/**
	 * @return la liste des classes de connexité du graphe
	 */
	public ArrayList<ArrayList<Integer>> calculerClassesDeConnexite() {

		boolean contains;
		ArrayList<ArrayList<Integer>> classes = new ArrayList<>(); //Liste de classe de connexité à retourner

		if (mat.length > 0) classes.add(calculerClasseDeConnexite(0));
		
		contains = false;
		for (int sommet = 1; sommet < mat.length; sommet++) { //Commence à 1 car la classe de 0 est déjà dans classes
			
			for (int i = 0; i < classes.size() && !contains; i++) { //On test toutes les classes existantes pour le moment
				
				if (classes.get(i).contains((sommet))) { //Si la classe[i] contient le sommet
					
					contains = true;
					break;
				}
			}
			if (!contains) {

				classes.add(calculerClasseDeConnexite(sommet)); //Ajoute la classe de connexité
			}
		}
		return classes;
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
	public boolean estUnIsthme(int u, int v) {
		
		return new Graphe(copy()).supprimerArete(u, v).nbCC() > nbCC();
	}

	public int[][] copy() {

		int[][] mat = new int[this.mat.length][this.mat.length];
		for (int i = 0; i < mat.length; i++) System.arraycopy(this.mat[i], 0, mat[i], 0, mat.length);
		return mat;
	}

	/**
	 * Calcule le plus long chemin présent dans le graphe
	 * @return une liste de sommets formant le plus long chemin dans le graphe
	 */
	public ArrayList<Integer> plusLongChemin() {
		
		ArrayList<ArrayList<Integer>> chemins = new ArrayList<>();
		ArrayList<Integer> classe = new ArrayList<>();
		ArrayList<Integer> voisins;
		Graphe graphe = new Graphe(copy());
		chemins.add(new ArrayList<>());
		int sommet = 0;

		for (ArrayList<Integer> list : calculerClassesDeConnexite()) {

			if (list.size() > classe.size()) classe = list;
		}
		for (Integer i : classe) {

			if (voisins(i).size() % 2 == 1) {

				sommet = i;
				break;
			}
		}
		do {
			
			chemins.get(0).add(0, sommet);
			voisins = graphe.voisins(sommet);
			
			if (!voisins.isEmpty()) {
				
				graphe.supprimerArete(sommet, voisins.get(0));
				sommet = voisins.get(0);
			}
			else {
				
				chemins.add(0, chemins.get(0));
				chemins.get(0).remove(chemins.get(1).get(0));
				voisins = graphe.voisins(chemins.get(0).get(1));
				voisins.remove(chemins.get(1).get(0));
				//graphe.ajouterArete(chemins.get(0).get(1), chemins.get(0).remove(0), 1);
			}
		}
		while (chemins.get(0).size() != 1 || !voisins.isEmpty()) ;
		for (int i = 0; i < chemins.size(); i++) {
			
			if (chemins.get(i).size() > chemins.get(0).size()) {
				
				chemins.set(0, chemins.get(i));
			}
		}
		Collections.reverse(chemins.get(0));
		return chemins.get(0);
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