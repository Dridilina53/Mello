import 'dart:convert';
import 'package:http/http.dart' as http;

class AuthService {
  // Android Emulator : 10.0.2.2 correspond à la machine hôte.
  // Pour un téléphone physique, remplacer par l'adresse IP locale du PC.
  static const String baseUrl = 'http://10.0.2.2:3000/api/auth';

  Future<Map<String, dynamic>> register({
    required String nom,
    required String prenom,
    required String email,
    required String password,
    required String dateNaissance,
    required String telephone,
    required String role,
  }) async {
    final parts = dateNaissance.split('/');
    if (parts.length != 3) {
      throw Exception('Format de date de naissance invalide.');
    }
    final sqlDate = '${parts[2]}-${parts[1]}-${parts[0]}';

    final response = await http
        .post(
          Uri.parse('$baseUrl/register'),
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode({
            'nom': nom.trim(),
            'prenom': prenom.trim(),
            'email': email.trim(),
            'password': password,
            'dateNaissance': sqlDate,
            'telephone': telephone.trim(),
            'role': role,
          }),
        )
        .timeout(const Duration(seconds: 15));

    final Map<String, dynamic> data =
        jsonDecode(response.body) as Map<String, dynamic>;

    if (response.statusCode != 201) {
      throw Exception(data['message'] ?? 'Échec de l’inscription.');
    }
    return data;
  }
}
