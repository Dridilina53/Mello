import '../models/seance.dart';

/// Données de test (remplacées plus tard par l'API / base de données).
List<Seance> buildSeancesDemo() {
  final demain = DateTime.now().add(const Duration(days: 1));
  DateTime at(int jours, int h, [int m = 0]) =>
      DateTime(demain.year, demain.month, demain.day + jours, h, m);

  return [
    Seance(
      id: '1',
      titre: 'Gestion du stress',
      specialite: 'Stress',
      coachNom: 'Rahma Ben Ali',
      imageUrl: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600',
      dateHeure: at(0, 9),
      dureeMinutes: 45,
      prix: 60,
    ),
    Seance(
      id: '2',
      titre: 'Confiance en soi',
      specialite: 'Confiance en soi',
      coachNom: 'Sami Trabelsi',
      imageUrl: 'https://images.unsplash.com/photo-1499209974431-9dddcece7f88?w=600',
      dateHeure: at(0, 14, 30),
      dureeMinutes: 60,
      prix: 80,
      enLigne: false,
    ),
    Seance(
      id: '3',
      titre: 'Équilibre vie pro / perso',
      specialite: 'Carrière',
      coachNom: 'Ines Gharbi',
      imageUrl: 'https://images.unsplash.com/photo-1522202176988-66273c2fd55f?w=600',
      dateHeure: at(1, 18),
      dureeMinutes: 45,
      prix: 70,
    ),
    Seance(
      id: '4',
      titre: 'Méditation guidée',
      specialite: 'Stress',
      coachNom: 'Sami Trabelsi',
      imageUrl: 'https://images.unsplash.com/photo-1545389336-cf090694435e?w=600',
      dateHeure: at(2, 8, 30),
      dureeMinutes: 30,
      prix: 40,
    ),
    Seance(
      id: '5',
      titre: 'Préparer un entretien',
      specialite: 'Carrière',
      coachNom: 'Rahma Ben Ali',
      imageUrl: 'https://images.unsplash.com/photo-1573497019940-1c28c88b4f3e?w=600',
      dateHeure: at(2, 16),
      dureeMinutes: 60,
      prix: 90,
    ),
  ];
}