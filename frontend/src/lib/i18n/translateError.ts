import { get } from 'svelte/store';
import { _ } from 'svelte-i18n';
import { ValidationError } from '$lib/api/glossarClient';

export function translateError(err: unknown, fallbackKey = 'request: failed'): string {
    const t = get(_);
    if (err instanceof ValidationError) {
        const localizedArgs = err.args.map((arg) => t(`fields.${arg}`, { default: arg }));
        const values = Object.fromEntries(localizedArgs.map((v, i) => [String(i), v]));
        return t(`errors.${err.key}`, { values, default: t(`errors.${fallbackKey}`) });
    }
    return t(`errors.${fallbackKey}`);
}
