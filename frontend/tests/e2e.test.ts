import { test, expect } from '@playwright/test';

test.describe('Öffentliche Seiten', () => {
  test('Startseite lädt korrekt', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/KarateAI/);
    await expect(page.locator('.navbar-brand')).toBeVisible();
  });

  test('Startseite zeigt Login- und Registrieren-Button', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('a.btn[href="/login"]')).toBeVisible();
    await expect(page.locator('a.btn[href="/signup"]')).toBeVisible();
  });

  test('Login-Seite ist erreichbar', async ({ page }) => {
    await page.goto('/login');
    await expect(page.locator('button[type="submit"]')).toBeVisible();
  });

  test('Registrierungsseite ist erreichbar', async ({ page }) => {
    await page.goto('/signup');
    await expect(page.locator('h1, h2').first()).toBeVisible();
  });

  test('Navigation von Startseite zur Login-Seite', async ({ page }) => {
    await page.goto('/');
    await page.locator('a.btn[href="/login"]').click();
    await expect(page).toHaveURL(/.*login/);
  });

  test('Navigation von Startseite zur Registrierungsseite', async ({ page }) => {
    await page.goto('/');
    await page.locator('a.btn[href="/signup"]').click();
    await expect(page).toHaveURL(/.*signup/);
  });

  test('Footer ist sichtbar auf der Startseite', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('.site-footer')).toBeVisible();
  });

  test('Geschützte Seiten leiten zur Login-Seite um', async ({ page }) => {
    await page.goto('/sportler');
    await expect(page).toHaveURL(/.*login/);
  });
});
