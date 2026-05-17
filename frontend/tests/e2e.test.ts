import { test, expect } from '@playwright/test';

test.describe('Öffentliche Seiten', () => {
  test('Startseite lädt korrekt', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/KarateAI/);
    await expect(page.locator('text=KarateAI Coach')).toBeVisible();
  });

  test('Startseite zeigt Login- und Registrieren-Button', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('a[href="/login"]').first()).toBeVisible();
    await expect(page.locator('a[href="/signup"]').first()).toBeVisible();
  });

  test('Login-Seite ist erreichbar', async ({ page }) => {
    await page.goto('/login');
    await expect(page.locator('text=Anmelden')).toBeVisible();
  });

  test('Registrierungsseite ist erreichbar', async ({ page }) => {
    await page.goto('/signup');
    await expect(page.locator('text=Registrieren')).toBeVisible();
  });

  test('Navigation von Startseite zur Login-Seite', async ({ page }) => {
    await page.goto('/');
    await page.locator('a[href="/login"]').first().click();
    await expect(page).toHaveURL(/.*login/);
  });

  test('Navigation von Startseite zur Registrierungsseite', async ({ page }) => {
    await page.goto('/');
    await page.locator('a[href="/signup"]').first().click();
    await expect(page).toHaveURL(/.*signup/);
  });

  test('Footer ist sichtbar auf der Startseite', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('text=ZHAW School of Management and Law')).toBeVisible();
  });

  test('Geschützte Seiten leiten zur Login-Seite um', async ({ page }) => {
    await page.goto('/sportler');
    await expect(page).toHaveURL(/.*login/);
  });
});
