
import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  private fb = inject(FormBuilder);
  private auth = inject(AuthService);
  private router = inject(Router);

  erreur = '';
  loading = false;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  onSubmit() {

    this.erreur = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password } = this.form.value;

    this.loading = true;

    this.auth.login(email!, password!).subscribe({

      next: (response) => {

        this.loading = false;

        console.log('LOGIN SUCCESS:', response);
localStorage.setItem('userId', response.userId.toString());
localStorage.setItem('email', response.email);
localStorage.setItem('role', response.role);
        // Redirect according to user role
        if (response.role === 'CLIENT') {
          this.router.navigate(['/client/dashboard']);
        }

        else if (response.role === 'COACH') {
          this.router.navigate(['/coach/dashboard']);
        }

        else if (response.role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        }

        else {
          this.erreur = 'Unknown user role.';
        }
      },

      error: (err) => {

        console.error('LOGIN ERROR:', err);

        this.loading = false;

        this.erreur =
          err.error?.message || 'Email or password incorrect.';
      }

    });
  }
}
