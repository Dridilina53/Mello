/// Une s├®ance de coaching propos├®e par un coach.
class Seance {
  final String id;
  final String titre;       // ex: "Gestion du stress"
  final String specialite;  // ex: "Stress", "Confiance en soi"...
  final String coachNom;    // ex: "Rahma Ben Ali"
  final String imageUrl;    // image de la card
  final DateTime dateHeure; // d├®but de la s├®ance
  final int dureeMinutes;   // ex: 45
  final double prix;        // en DT
  final bool enLigne;       // visio ou pr├®sentiel
  bool reservee;            // devient true quand le client r├®serve

  Seance({
    required this.id,
    required this.titre,
    required this.specialite,
    required this.coachNom,
    required this.imageUrl,
    required this.dateHeure,
    required this.dureeMinutes,
    required this.prix,
    this.enLigne = true,
    this.reservee = false,
  });

  /// Date lisible, ex : "22/11 ├á 09:00" (utile pour la recherche).
  String get dateTexte {
    String deux(int n) => n.toString().padLeft(2, '0');
    return '${deux(dateHeure.day)}/${deux(dateHeure.month)} ├á '
        '${deux(dateHeure.hour)}:${deux(dateHeure.minute)}';
  }
}
