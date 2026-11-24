package com.example.espacecoach


/**
 * Faux backend : il joue le rôle du serveur en attendant l'API de l'équipe.
 * Tout est gardé en mémoire (les données disparaissent quand on ferme l'application).
 *
 * "object" = une seule instance, utilisable partout : FakeBackend.login(...)
 */
object FakeBackend {

    // Les coachs qui ont un compte (ici un seul, pour tester)
    private val coachs = listOf(
        Coach(nom = "Rahma Ben Ali", email = "coach@mello.tn", motDePasse = "123456")
    )

    // Le coach actuellement connecté (null = personne)
    var coachConnecte: Coach? = null
        private set // on peut le lire partout, mais seul FakeBackend le modifie

    /** Vérifie l'e-mail et le mot de passe. Renvoie le coach, ou null si c'est faux. */
    fun login(email: String, motDePasse: String): Coach? {
        val coach = coachs.find { it.email == email && it.motDePasse == motDePasse }
        coachConnecte = coach
        return coach
    }

    // ---------- Les séances ----------

    // La "base de données" : la liste de toutes les séances enregistrées
    private val seances = mutableListOf<Seance>()
    private var prochainId = 1

    /** Enregistre une nouvelle séance pour le coach connecté. */
    fun saveSeance(
        titre: String, specialite: String, date: String, heure: String,
        dureeMinutes: Int, prix: Int, mode: String
    ): Seance? {
        val coach = coachConnecte ?: return null // personne n'est connecté : on refuse
        val seance = Seance(
            id = prochainId++,
            coachEmail = coach.email,
            coachNom = coach.nom,
            titre = titre,
            specialite = specialite,
            date = date,
            heure = heure,
            dureeMinutes = dureeMinutes,
            prix = prix,
            mode = mode
        )
        seances.add(seance)
        return seance
    }

    /** Renvoie les séances du coach connecté. */
    fun getSeances(): List<Seance> {
        val coach = coachConnecte ?: return emptyList()
        return seances.filter { it.coachEmail == coach.email }
    }

    // ---------- Les demandes des clients ----------

    // Quelques demandes de test (plus tard, elles viendront des clients)
    private val demandes = mutableListOf(
        Demande(1, "coach@mello.tn", "Sami Trabelsi", "Gérer le stress au travail", "28/11/2026", "10:00"),
        Demande(2, "coach@mello.tn", "Amira Gharbi", "Confiance en soi", "29/11/2026", "14:30"),
        Demande(3, "coach@mello.tn", "Yassine Jlassi", "Orientation de carrière", "30/11/2026", "09:00")
    )

    /** Renvoie les demandes reçues par le coach connecté. */
    fun getDemandes(): List<Demande> {
        val coach = coachConnecte ?: return emptyList()
        return demandes.filter { it.coachEmail == coach.email }
    }

    /** Le coach accepte une demande. */
    fun accepterDemande(id: Int) {
        val coach = coachConnecte ?: return
        val position = demandes.indexOfFirst { it.id == id && it.coachEmail == coach.email }
        if (position >= 0) {
            // une data class ne se modifie pas : on en fait une copie avec acceptee = true
            demandes[position] = demandes[position].copy(acceptee = true)
        }
    }

    /** Le coach supprime une demande. */
    fun supprimerDemande(id: Int) {
        val coach = coachConnecte ?: return
        demandes.removeAll { it.id == id && it.coachEmail == coach.email }
    }

    /** Déconnecte le coach : plus personne n'est connecté. */
    fun deconnecter() {
        coachConnecte = null
    }
}