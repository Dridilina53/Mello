import 'package:flutter/material.dart';
import '../data/seances_data.dart';
import '../models/seance.dart';
import '../theme/app_theme.dart';
import '../widgets/seance_card.dart';

class ReservationPage extends StatefulWidget {
  const ReservationPage({super.key});

  @override
  State<ReservationPage> createState() => _ReservationPageState();
}

class _ReservationPageState extends State<ReservationPage> {
  // Toutes les s├®ances (plus tard : charg├®es depuis l'API)
  final List<Seance> _seances = buildSeancesDemo();

  // --- ├ëtat de la recherche et des filtres ---
  final TextEditingController _rechercheCtrl = TextEditingController();
  String _recherche = '';
  String? _specialite; // null = toutes les sp├®cialit├®s
  String? _coach; // null = tous les coachs
  String _creneau = 'Tous'; // Tous / Matin / Apr├¿s-midi / Soir

  static const List<String> _creneaux = ['Tous', 'Matin', 'Apr├¿s-midi', 'Soir'];

  @override
  void dispose() {
    _rechercheCtrl.dispose();
    super.dispose();
  }

  // Listes pour les filtres (sans doublons, tri├®es)
  List<String> get _specialites =>
      (_seances.map((s) => s.specialite).toSet().toList()..sort());
  List<String> get _coachs =>
      (_seances.map((s) => s.coachNom).toSet().toList()..sort());

  // Le cr├®neau horaire correspond-il au filtre choisi ?
  bool _creneauOk(Seance s) {
    final h = s.dateHeure.hour;
    switch (_creneau) {
      case 'Matin':
        return h < 12;
      case 'Apr├¿s-midi':
        return h >= 12 && h < 17;
      case 'Soir':
        return h >= 17;
      default:
        return true;
    }
  }

  // S├®ances qui respectent la recherche ET tous les filtres
  List<Seance> get _resultats {
    final q = _recherche.trim().toLowerCase();
    return _seances.where((s) {
      final texte =
          '${s.titre} ${s.coachNom} ${s.specialite} ${s.dateTexte}'.toLowerCase();
      return texte.contains(q) &&
          (_specialite == null || s.specialite == _specialite) &&
          (_coach == null || s.coachNom == _coach) &&
          _creneauOk(s);
    }).toList();
  }

  void _reinitialiser() {
    setState(() {
      _rechercheCtrl.clear();
      _recherche = '';
      _specialite = null;
      _coach = null;
      _creneau = 'Tous';
    });
  }

  // --- R├®servation : confirmation, puis la s├®ance passe en "R├®serv├®e" ---
  Future<void> _reserver(Seance seance) async {
    final confirme = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: Colors.white,
        surfaceTintColor: Colors.transparent,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        title: const Text(
          'Confirmer la r├®servation',
          style: TextStyle(fontWeight: FontWeight.bold, color: AppColors.texte),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(seance.titre,
                style: const TextStyle(
                    fontWeight: FontWeight.bold,
                    fontSize: 16,
                    color: AppColors.marron)),
            const SizedBox(height: 8),
            Text('Coach : ${seance.coachNom}'),
            Text('Date : ${seance.dateTexte}'),
            Text('Dur├®e : ${seance.dureeMinutes} min'),
            Text('Mode : ${seance.enLigne ? "En ligne" : "Pr├®sentiel"}'),
            Text('Prix : ${seance.prix.toStringAsFixed(0)} DT'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('Annuler'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('Confirmer'),
          ),
        ],
      ),
    );

    if (confirme != true) return; // le client a annul├®
    if (!mounted) return;

    setState(() => seance.reservee = true);

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
            'R├®servation confirm├®e : ${seance.titre} avec ${seance.coachNom}'),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final resultats = _resultats;

    return Scaffold(
      // Comme les pages de connexion : juste la fl├¿che retour, sans titre
      appBar: AppBar(),
      body: CustomScrollView(
        slivers: [
          // En-t├¬te + recherche + filtres (d├®filent avec la page)
          SliverToBoxAdapter(
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text(
                    'R├®server une s├®ance',
                    style: TextStyle(
                      fontSize: 28,
                      fontWeight: FontWeight.bold,
                      color: AppColors.texte,
                    ),
                  ),
                  const SizedBox(height: 8),
                  const Text(
                    'Choisissez le coach et la s├®ance qui vous conviennent.',
                    style: TextStyle(
                        fontSize: 16, color: AppColors.texteSecondaire),
                  ),
                  const SizedBox(height: 20),
                  _buildRecherche(),
                  const SizedBox(height: 16),
                  _buildFiltres(),
                  const SizedBox(height: 8),
                ],
              ),
            ),
          ),

          // R├®sultats
          if (resultats.isEmpty)
            const SliverFillRemaining(
              hasScrollBody: false,
              child: Center(
                child: Text('Aucune s├®ance trouv├®e',
                    style: TextStyle(color: AppColors.texteSecondaire)),
              ),
            )
          else
            SliverPadding(
              padding: const EdgeInsets.fromLTRB(24, 8, 24, 24),
              sliver: SliverGrid(
                gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
                  maxCrossAxisExtent: 340,
                  mainAxisExtent: 370,
                  crossAxisSpacing: 16,
                  mainAxisSpacing: 16,
                ),
                delegate: SliverChildBuilderDelegate(
                  (context, index) {
                    final seance = resultats[index];
                    return SeanceCard(
                      seance: seance,
                      onReserver: () => _reserver(seance),
                    );
                  },
                  childCount: resultats.length,
                ),
              ),
            ),
        ],
      ),
    );
  }

  // --- Barre de recherche (style des champs des autres pages) ---
  Widget _buildRecherche() {
    return TextField(
      controller: _rechercheCtrl,
      onChanged: (v) => setState(() => _recherche = v),
      decoration: InputDecoration(
        hintText: 'Rechercher un coach, une sp├®cialit├®, une heure...',
        prefixIcon: const Icon(Icons.search, color: AppColors.marron),
        suffixIcon: _recherche.isEmpty
            ? null
            : IconButton(
                icon: const Icon(Icons.clear, color: AppColors.marron),
                onPressed: () => setState(() {
                  _rechercheCtrl.clear();
                  _recherche = '';
                }),
              ),
      ),
    );
  }

  // Petit titre en gras au-dessus d'un champ (comme "Adresse e-mail")
  Widget _label(String texte) => Padding(
        padding: const EdgeInsets.only(bottom: 6),
        child: Text(texte,
            style: const TextStyle(
                fontWeight: FontWeight.w600, color: AppColors.texte)),
      );

  // --- Filtres : sp├®cialit├® (chips), coach et cr├®neau (listes) ---
  Widget _buildFiltres() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _label('Sp├®cialit├®'),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: [
            for (final sp in _specialites)
              ChoiceChip(
                label: Text(sp),
                selected: _specialite == sp,
                showCheckmark: false,
                selectedColor: AppColors.marron,
                backgroundColor: Colors.white,
                labelStyle: TextStyle(
                  fontWeight: FontWeight.w600,
                  color: _specialite == sp ? Colors.white : AppColors.texte,
                ),
                side: BorderSide(
                  color:
                      _specialite == sp ? AppColors.marron : AppColors.bordure,
                ),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(20)),
                // re-cliquer sur la chip s├®lectionn├®e retire le filtre
                onSelected: (sel) =>
                    setState(() => _specialite = sel ? sp : null),
              ),
          ],
        ),
        const SizedBox(height: 16),
        Wrap(
          spacing: 16,
          runSpacing: 12,
          crossAxisAlignment: WrapCrossAlignment.end,
          children: [
            SizedBox(
              width: 220,
              child: _liste<String?>(
                label: 'Coach',
                valeur: _coach,
                items: [
                  const DropdownMenuItem(
                      value: null, child: Text('Tous les coachs')),
                  for (final c in _coachs)
                    DropdownMenuItem(value: c, child: Text(c)),
                ],
                onChanged: (v) => setState(() => _coach = v),
              ),
            ),
            SizedBox(
              width: 220,
              child: _liste<String?>(
                label: 'Moment de la journ├®e',
                valeur: _creneau,
                items: [
                  for (final c in _creneaux)
                    DropdownMenuItem(value: c, child: Text(c)),
                ],
                onChanged: (v) => setState(() => _creneau = v ?? 'Tous'),
              ),
            ),
            TextButton.icon(
              onPressed: _reinitialiser,
              icon: const Icon(Icons.restart_alt),
              label: const Text('R├®initialiser'),
            ),
          ],
        ),
      ],
    );
  }

  // Liste d├®roulante : m├¬me style que les champs (blanc, arrondi, bordure beige)
  Widget _liste<T>({
    required String label,
    required T valeur,
    required List<DropdownMenuItem<T>> items,
    required ValueChanged<T?> onChanged,
  }) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        _label(label),
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 14),
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(16),
            border: Border.all(color: AppColors.bordure),
          ),
          child: DropdownButtonHideUnderline(
            child: DropdownButton<T>(
              value: valeur,
              isExpanded: true,
              borderRadius: BorderRadius.circular(16),
              dropdownColor: Colors.white,
              icon: const Icon(Icons.keyboard_arrow_down,
                  color: AppColors.marron),
              items: items,
              onChanged: onChanged,
            ),
          ),
        ),
      ],
    );
  }
}
