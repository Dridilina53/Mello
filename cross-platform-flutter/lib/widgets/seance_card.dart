import 'package:flutter/material.dart';
import '../models/seance.dart';
import '../theme/app_theme.dart';

/// Card affichant une s├®ance, son coach et une image.
class SeanceCard extends StatelessWidget {
  final Seance seance;
  final VoidCallback onReserver; // appel├® quand on clique sur "R├®server"

  const SeanceCard({
    super.key,
    required this.seance,
    required this.onReserver,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      color: Colors.white,
      elevation: 0,
      margin: EdgeInsets.zero, // l'espacement est g├®r├® par la grille
      clipBehavior: Clip.antiAlias, // arrondit aussi l'image
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(20),
        side: const BorderSide(color: AppColors.bordure),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // --- Image ---
          SizedBox(
            height: 160,
            width: double.infinity,
            child: Image.network(
              seance.imageUrl,
              fit: BoxFit.cover,
              // Si pas de connexion : une ic├┤ne ├á la place
              errorBuilder: (context, error, stack) => Container(
                color: AppColors.beige,
                child: const Icon(Icons.self_improvement,
                    size: 60, color: AppColors.marron),
              ),
            ),
          ),

          Padding(
            padding: const EdgeInsets.all(14),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // --- Titre ---
                Text(
                  seance.titre,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: AppColors.texte,
                  ),
                ),
                const SizedBox(height: 8),

                // --- Sp├®cialit├® ---
                Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                  decoration: BoxDecoration(
                    color: AppColors.beige,
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: Text(
                    seance.specialite,
                    style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      color: AppColors.marron,
                    ),
                  ),
                ),
                const SizedBox(height: 10),

                // --- Coach ---
                Row(children: [
                  const Icon(Icons.person_outline,
                      size: 18, color: AppColors.marron),
                  const SizedBox(width: 6),
                  Expanded(
                    child: Text(
                      seance.coachNom,
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(color: AppColors.texteSecondaire),
                    ),
                  ),
                ]),
                const SizedBox(height: 4),

                // --- Date / dur├®e / mode ---
                Row(children: [
                  const Icon(Icons.schedule,
                      size: 18, color: AppColors.marron),
                  const SizedBox(width: 6),
                  Expanded(
                    child: Text(
                      '${seance.dateTexte} ÔÇó '
                      '${seance.dureeMinutes} min ÔÇó '
                      '${seance.enLigne ? "En ligne" : "Pr├®sentiel"}',
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(color: AppColors.texteSecondaire),
                    ),
                  ),
                ]),
                const SizedBox(height: 12),

                // --- Prix + bouton ---
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      '${seance.prix.toStringAsFixed(0)} DT',
                      style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: AppColors.marron,
                      ),
                    ),
                    ElevatedButton(
                      // onPressed null = bouton d├®sactiv├®
                      onPressed: seance.reservee ? null : onReserver,
                      child: Text(seance.reservee ? 'R├®serv├®e' : 'R├®server'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
