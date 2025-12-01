#!/usr/bin/env python3
"""
Verum Omnis - Generate Rule Assets
Creates JSON configuration files for the forensic engine.
"""

import json
import os
from pathlib import Path

def create_rule_assets():
    assets_dir = Path("app/src/main/assets/rules")
    assets_dir.mkdir(parents=True, exist_ok=True)

    # Dishonesty Matrix
    dishonesty_matrix = {
        "version": "1.0",
        "contradictions": {
            "weight": 3,
            "examples": ["Opposing statements vs evidence"],
            "patterns": [
                "no deal.*invoice",
                "denied.*admitted",
                "refused.*accepted",
                "never.*always"
            ]
        },
        "omissions": {
            "weight": 2,
            "examples": ["Cropped screenshots"],
            "patterns": [
                "selective.*edit",
                "missing.*context",
                "cropped.*screenshot"
            ]
        },
        "manipulations": {
            "weight": 3,
            "examples": ["Altered documents"],
            "patterns": [
                "modified.*original",
                "altered.*date"
            ]
        }
    }

    with open(assets_dir / "dishonesty_matrix.json", "w") as f:
        json.dump(dishonesty_matrix, f, indent=2)
    print(f"✅ Created: {assets_dir}/dishonesty_matrix.json")

    # Extraction Protocol
    extraction_protocol = {
        "version": "1.0",
        "step1_keywords": [
            "admin", "deny", "forged", "access", "delete",
            "refuse", "invoice", "profit", "transfer", "payment"
        ],
        "step2_tags": [
            "#Cybercrime", "#Fraud", "#Oppression", "#FiduciaryBreach"
        ],
        "step3_scoring": {
            "low": {"weight": 1, "color": "#4CAF50"},
            "medium": {"weight": 2, "color": "#FF9800"},
            "high": {"weight": 3, "color": "#F44336"}
        }
    }

    with open(assets_dir / "extraction_protocol.json", "w") as f:
        json.dump(extraction_protocol, f, indent=2)
    print(f"✅ Created: {assets_dir}/extraction_protocol.json")

    print("\n✅ Rule assets generated successfully!")

if __name__ == "__main__":
    create_rule_assets()
