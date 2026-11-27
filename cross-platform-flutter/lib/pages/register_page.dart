import 'package:flutter/material.dart';

import '../constants/app_colors.dart';
import '../widgets/mello_logo.dart';
import '../widgets/mello_button.dart';
import '../widgets/mello_text_field.dart';
import '../services/auth_service.dart';
import 'sign_in_page.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _formKey = GlobalKey<FormState>();

  final _nomController = TextEditingController();
  final _prenomController = TextEditingController();
  final _emailController = TextEditingController();
  final _telephoneController = TextEditingController();
  final _dateController = TextEditingController();
  final _villeController = TextEditingController();
  final _passwordController = TextEditingController();
  final _confirmPasswordController = TextEditingController();

  bool _hidePassword = true;
  bool _hideConfirmPassword = true;

  @override
  void dispose() {
    _nomController.dispose();
    _prenomController.dispose();
    _emailController.dispose();
    _telephoneController.dispose();
    _dateController.dispose();
    _villeController.dispose();
    _passwordController.dispose();
    _confirmPasswordController.dispose();
    super.dispose();
  }

  Future<void> _selectDate() async {
    final DateTime? pickedDate = await showDatePicker(
      context: context,
      initialDate: DateTime(2000),
      firstDate: DateTime(1920),
      lastDate: DateTime.now(),
      locale: const Locale('fr', 'FR'),
      helpText: 'Sélectionner votre date de naissance',
      cancelText: 'Annuler',
      confirmText: 'Confirmer',
    );

    if (pickedDate != null) {
      setState(() {
        final day = pickedDate.day.toString().padLeft(2, '0');
        final month = pickedDate.month.toString().padLeft(2, '0');
        final year = pickedDate.year;

        _dateController.text = '$day/$month/$year';
      });
    }
  }

  Future<void> _register() async {
  if (!_formKey.currentState!.validate()) {
    return;
  }

  try {
    final dateParts = _dateController.text.split('/');

    final dateNaissance =
        '${dateParts[2]}-${dateParts[1]}-${dateParts[0]}';

    final result = await AuthService().register({
      'nom': _nomController.text.trim(),
      'prenom': _prenomController.text.trim(),
      'email': _emailController.text.trim(),
      'telephone': _telephoneController.text.trim(),
      'dateNaissance': dateNaissance,
      'role': 'CLIENT',
      'password': _passwordController.text,
    });

    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
          result['message'] ?? 'Inscription réussie !',
        ),
      ),
    );

    Navigator.pushReplacement(
      context,
      MaterialPageRoute(
        builder: (context) => const SignInPage(),
      ),
    );
  } catch (e) {
    if (!mounted) return;

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(
          e.toString().replaceFirst('Exception: ', ''),
        ),
        backgroundColor: Colors.red,
      ),
    );
  }
}
  String? _requiredValidator(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'Ce champ est obligatoire';
    }
    return null;
  }

  String? _emailValidator(String? value) {
    if (value == null || value.trim().isEmpty) {
      return 'Veuillez saisir votre adresse e-mail';
    }
    if (!RegExp(r'^[^@]+@[^@]+\.[^@]+$')
        .hasMatch(value.trim())) {
      return 'Adresse e-mail invalide';
    }
    return null;
  }

  String? _passwordValidator(String? value) {
    if (value == null || value.isEmpty) {
      return 'Veuillez saisir un mot de passe';
    }
    if (value.length < 8) {
      return '8 caractères minimum';
    }
    return null;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: MelloColors.linen,
        foregroundColor: MelloColors.espresso,
        elevation: 0,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 25),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const SizedBox(height: 10),

                const Center(
                  child: MelloLogo(
                    iconSize: 100,
                  ),
                ),

                const SizedBox(height: 30),

                const Text(
                  'Créer un compte',
                  style: TextStyle(
                    fontSize: 27,
                    fontWeight: FontWeight.bold,
                    color: MelloColors.espresso,
                  ),
                ),

                const SizedBox(height: 8),

                const Text(
                  'Rejoignez Mello et prenez soin de votre bien-être.',
                  style: TextStyle(
                    fontSize: 14,
                    color: MelloColors.cocoa,
                  ),
                ),

                const SizedBox(height: 25),

                // Nom
                MelloTextField(
                  label: 'Nom',
                  hint: 'Votre nom',
                  controller: _nomController,
                  validator: _requiredValidator,
                ),

                const SizedBox(height: 18),

                // Prénom
                MelloTextField(
                  label: 'Prénom',
                  hint: 'Votre prénom',
                  controller: _prenomController,
                  validator: _requiredValidator,
                ),

                const SizedBox(height: 18),

                // Adresse e-mail
                MelloTextField(
                  label: 'Adresse e-mail',
                  hint: 'exemple@email.com',
                  controller: _emailController,
                  keyboardType: TextInputType.emailAddress,
                  validator: _emailValidator,
                ),

                const SizedBox(height: 18),

                // Téléphone
                MelloTextField(
                  label: 'Téléphone',
                  hint: 'Votre numéro de téléphone',
                  controller: _telephoneController,
                  keyboardType: TextInputType.phone,
                  validator: (value) {
                    if (value == null || value.trim().isEmpty) {
                      return 'Veuillez saisir votre numéro';
                    }
                    if (value.trim().length < 8) {
                      return 'Numéro de téléphone invalide';
                    }
                    return null;
                  },
                ),

                const SizedBox(height: 18),

                // Date de naissance
                MelloTextField(
                  label: 'Date de naissance',
                  hint: 'JJ/MM/AAAA',
                  controller: _dateController,
                  readOnly: true,
                  onTap: _selectDate,
                  suffixIcon: const Icon(
                    Icons.calendar_month_outlined,
                    color: MelloColors.cocoa,
                  ),
                  validator: _requiredValidator,
                ),

                const SizedBox(height: 18),

                // Ville
                MelloTextField(
                  label: 'Ville',
                  hint: 'Votre ville',
                  controller: _villeController,
                  validator: _requiredValidator,
                ),

                const SizedBox(height: 18),

                MelloTextField(
                  label: 'Mot de passe',
                  hint: '8 caractères minimum',
                  controller: _passwordController,
                  obscureText: _hidePassword,
                  suffixIcon: IconButton(
                    icon: Icon(
                      _hidePassword
                          ? Icons.visibility_off_outlined
                          : Icons.visibility_outlined,
                      color: MelloColors.cocoa,
                    ),
                    onPressed: () {
                      setState(() {
                        _hidePassword = !_hidePassword;
                      });
                    },
                  ),
                  validator: _passwordValidator,
                ),

                const SizedBox(height: 18),

                MelloTextField(
                  label: 'Confirmer le mot de passe',
                  hint: 'Répétez votre mot de passe',
                  controller: _confirmPasswordController,
                  obscureText: _hideConfirmPassword,
                  suffixIcon: IconButton(
                    icon: Icon(
                      _hideConfirmPassword
                          ? Icons.visibility_off_outlined
                          : Icons.visibility_outlined,
                      color: MelloColors.cocoa,
                    ),
                    onPressed: () {
                      setState(() {
                        _hideConfirmPassword = !_hideConfirmPassword;
                      });
                    },
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return 'Veuillez confirmer votre mot de passe';
                    }
                    if (value != _passwordController.text) {
                      return 'Les mots de passe ne correspondent pas';
                    }
                    return null;
                  },
                ),

                const SizedBox(height: 30),

                MelloButton(
                  text: 'Créer mon compte',
                  onPressed: _register,
                ),

                const SizedBox(height: 20),

                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Flexible(
                      child: Text(
                        'Vous avez déjà un compte ?',
                        style: TextStyle(
                          color: MelloColors.espresso,
                        ),
                      ),
                    ),
                    TextButton(
                      onPressed: () {
                        Navigator.pushReplacement(
                          context,
                          MaterialPageRoute(
                            builder: (context) => const SignInPage(),
                          ),
                        );
                      },
                      child: const Text(
                        'Se connecter',
                        style: TextStyle(
                          color: MelloColors.cocoa,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ],
                ),

                const SizedBox(height: 25),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
