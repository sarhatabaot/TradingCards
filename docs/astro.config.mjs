// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';

export default defineConfig({
	site: 'https://sarhatabaot.github.io',
	base: '/TradingCards',
	integrations: [
		starlight({
			title: 'TradingCards Docs',
			social: [
				{ icon: 'github', label: 'GitHub', href: 'https://github.com/sarhatabaot/TradingCards' },
				{ icon: 'discord', label: 'Discord', href: 'https://discord.gg/4v9gsBCgg8' },
				{ icon: 'external', label: 'Modrinth', href: 'https://modrinth.com/plugin/tradingcards' },
			],
			components: {
				SocialIcons: './src/components/SocialIcons.astro',
			},
			sidebar: [
				{
					label: 'Overview',
					items: [
						{ label: 'Trading Cards', slug: '' },
						{ label: 'FAQ', slug: 'faq' },
					],
				},
				{
					label: 'Customizing',
					items: [
						{ label: 'Getting Started', slug: 'customizing/getting-started' },
						{
							label: 'Settings',
							items: [
								{ label: 'Overview', slug: 'customizing/settings' },
								{ label: 'General', slug: 'customizing/settings/general' },
								{ label: 'Chances', slug: 'customizing/settings/chances' },
								{ label: 'Messages', slug: 'customizing/settings/messages' },
								{ label: 'Storage', slug: 'customizing/settings/storage' },
								{ label: 'Advanced', slug: 'customizing/settings/advanced' },
							],
						},
						{
							label: 'Customizing',
							items: [
								{ label: 'Overview', slug: 'customizing/customizing' },
								{ label: 'Cards', slug: 'customizing/customizing/cards' },
								{ label: 'Rarities', slug: 'customizing/customizing/rarities' },
								{ label: 'Series', slug: 'customizing/customizing/series' },
								{ label: 'Custom Types', slug: 'customizing/customizing/custom-types' },
								{ label: 'Upgrades', slug: 'customizing/customizing/upgrades' },
								{ label: 'Drop Pools', slug: 'customizing/customizing/drop-pools' },
							],
						},
						{
							label: 'Addons',
							items: [
								{ label: 'Overview', slug: 'customizing/addons' },
								{ label: 'Official Addons', slug: 'customizing/addons/official-addons' },
								{ label: 'Community Addons', slug: 'customizing/addons/community-addons' },
							],
						},
					],
				},
				{
					label: 'Commands',
					items: [
						{
							label: 'User Commands',
							items: [
								{ label: 'Overview', slug: 'commands/user-commands' },
								{ label: 'Worth', slug: 'commands/user-commands/worth' },
								{ label: 'Deck', slug: 'commands/user-commands/deck' },
								{ label: 'Info', slug: 'commands/user-commands/info' },
								{ label: 'Upgrade', slug: 'commands/user-commands/upgrade' },
								{ label: 'List', slug: 'commands/user-commands/list' },
								{ label: 'Buy', slug: 'commands/user-commands/buy' },
								{ label: 'Sell', slug: 'commands/user-commands/sell' },
							],
						},
						{
							label: 'Admin Commands',
							items: [
								{ label: 'Overview', slug: 'commands/admin-commands' },
								{ label: 'Resolve', slug: 'commands/admin-commands/resolve' },
								{ label: 'Migrate', slug: 'commands/admin-commands/migrate' },
								{ label: 'Giveaway', slug: 'commands/admin-commands/giveaway' },
								{ label: 'Give', slug: 'commands/admin-commands/give' },
								{ label: 'Debug', slug: 'commands/admin-commands/debug' },
								{ label: 'Create', slug: 'commands/admin-commands/create' },
								{ label: 'Edit', slug: 'commands/admin-commands/edit' },
							],
						},
						{ label: 'All Commands', slug: 'commands/all-commands' },
						{ label: 'All Permissions', slug: 'commands/all-permissions' },
					],
				},
				{
					label: 'Compatibility',
					items: [{ label: 'Placeholders', slug: 'plugin-compatibility/placeholders' }],
				},
				{
					label: 'Migration',
					items: [
						{ label: '5.0.4 -> 5.7.2+', slug: 'migration/5.0.4-greater-than-5.7.2+' },
						{ label: '5.0.4 -> 5.4+', slug: 'migration/5.0.4-greater-than-5.4+' },
						{ label: '5.5.x -> 5.6.x YAML', slug: 'migration/5.5.x-greater-than-5.6.x-yaml' },
						{ label: '5.6.x YAML -> SQL', slug: 'migration/5.6.x-yaml-greater-than-sql' },
					],
				},
				{
					label: 'Developers',
					items: [{ label: 'Java API', slug: 'developers/java-api' }],
				},
				{
					label: 'Support',
					items: [
						{ label: 'Overview', slug: 'support' },
						{ label: 'Discord', slug: 'support/discord' },
						{ label: 'Report a Bug', slug: 'support/report-a-bug' },
					],
				},
			],
		}),
	],
});
