import 'package:flutter/material.dart';

/// Couleurs de l'application Mello (relevées sur les écrans de connexion).
class AppColors {
  static const fond = Color(0xFFF5F0EA); // fond beige des pages
  static const marron = Color(0xFF7D5A44); // boutons et accents
  static const texte = Color(0xFF3D302A); // titres et labels
  static const texteSecondaire = Color(0xFF66554D); // sous-titres
  static const placeholder = Color(0xFF9F9188); // texte indicatif des champs
  static const bordure = Color(0xFFE3D8CA); // bordure des champs et cards
  static const beige = Color(0xFFEDE3D6); // fond des petites étiquettes
}

/// Thème commun : à utiliser dans MaterialApp pour que toutes les pages
/// (connexion, inscription, réservation...) aient le même style.
class AppTheme {
  static OutlineInputBorder _bordure(Color couleur, [double largeur = 1]) =>
      OutlineInputBorder(
        borderRadius: BorderRadius.circular(16),
        borderSide: BorderSide(color: couleur, width: largeur),
      );

  static ThemeData get theme {
    final scheme = ColorScheme.fromSeed(seedColor: AppColors.marron).copyWith(
      primary: AppColors.marron,
      onPrimary: Colors.white,
      surface: AppColors.fond,
      onSurface: AppColors.texte,
    );

    return ThemeData(
      useMaterial3: true,
      colorScheme: scheme,
      scaffoldBackgroundColor: AppColors.fond,
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.fond,
        foregroundColor: AppColors.texte,
        elevation: 0,
        scrolledUnderElevation: 0,
        surfaceTintColor: Colors.transparent,
      ),
      // Champs de saisie : blancs, arrondis, bordure beige
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: Colors.white,
        hintStyle: const TextStyle(color: AppColors.placeholder),
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 16, vertical: 18),
        border: _bordure(AppColors.bordure),
        enabledBorder: _bordure(AppColors.bordure),
        focusedBorder: _bordure(AppColors.marron, 1.5),
      ),
      // Boutons : marron, arrondis, texte blanc en gras
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: AppColors.marron,
          foregroundColor: Colors.white,
          disabledBackgroundColor: AppColors.beige,
          disabledForegroundColor: AppColors.marron,
          elevation: 0,
          minimumSize: const Size(0, 44),
          padding: const EdgeInsets.symmetric(horizontal: 20),
          textStyle: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold),
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: AppColors.marron,
          textStyle: const TextStyle(fontWeight: FontWeight.bold),
        ),
      ),
      snackBarTheme: SnackBarThemeData(
        backgroundColor: AppColors.marron,
        contentTextStyle: const TextStyle(color: Colors.white),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      ),
    );
  }
}