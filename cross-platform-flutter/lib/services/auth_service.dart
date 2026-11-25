
import 'dart:convert';
import 'package:http/http.dart' as http;

class AuthService {
  static const String baseUrl = 'http://10.0.2.2:8080';

  Future<Map<String, dynamic>> login(
    String email,
    String password,
  ) async {
    final url = Uri.parse('$baseUrl/api/auth/login');

    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
      },
      body: jsonEncode({
        'email': email,
        'password': password,
      }),
    );

    final data = jsonDecode(response.body);

    if (response.statusCode == 200) {
      return data;
    } else {
      throw Exception(data['message'] ?? 'Erreur de connexion');
    }
  }

  Future<Map<String, dynamic>> register(
    Map<String, dynamic> userData,
  ) async {
    final url = Uri.parse('$baseUrl/api/auth/register');

    final response = await http.post(
      url,
      headers: {
        'Content-Type': 'application/json',
      },
      body: jsonEncode(userData),
    );

    final data = jsonDecode(response.body);

    if (response.statusCode == 201) {
      return data;
    } else {
      throw Exception(data['message'] ?? 'Erreur lors de l’inscription');
    }
  }
}
